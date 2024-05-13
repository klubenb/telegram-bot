package ru.skillfactory.homework.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.skillfactory.homework.telegrambot.entity.ActiveChat;
import ru.skillfactory.homework.telegrambot.repository.ActiveChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static ru.skillfactory.homework.telegrambot.TelegramBotCommands.*;


@Service //Данный класс является сервисом
@Slf4j //Подключаем логирование из Lombok'a
@RequiredArgsConstructor
public class BotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {


    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private CentralRussianBankService centralRussianBankService;
    @Autowired
    private ActiveChatRepository activeChatRepository;
    @Autowired
    private FinanceService financeService;

    @Value("${bot.api.key}")
    //Сюда будет вставлено значение из application.properties, в котором будет указан api key, полученный от BotFather
    private String apiKey;

    private Map<Long, List<String>> previousCommands = new ConcurrentHashMap<>();

    @Override
    public String getBotToken() {
        return apiKey;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        StringBuilder responseText = new StringBuilder();

        if (update.getMessage().hasText() && update.hasMessage()) {
            // Set variables

            Long chatId = update.getMessage().getChatId();
            //Это основной метод, который связан с обработкой сообщений
            try {
                //Тут начинается самое интересное - мы сравниваем, что прислал пользователь, и какие команды мы можем обработать. Пока что у нас только одна команда
                if (update.getMessage().getText().equalsIgnoreCase(CURRENT_RATES)) {
                    log.info("Get currentRates");
                    //Получаем все курсы валют на текущий момент и проходимся по ним в цикле
                    //В данной строчке мы собираем наше текстовое сообщение
                    centralRussianBankService.getCurrenciesFromCbr().stream()
                            .map(valuteCursOnDate -> valuteCursOnDate.getName() + " - " + valuteCursOnDate.getCourse() + "\n")
                            .forEach(responseText::append);
                } else if (ADD_INCOME.equalsIgnoreCase(update.getMessage().getText())) {
                    log.info("Add income");
                    responseText.append("Отправьте мне сумму полученного дохода");
                } else if (ADD_SPEND.equalsIgnoreCase(update.getMessage().getText())) {
                    log.info("Add spend");
                    responseText.append("Отправьте мне сумму расходов");
                } else {
                    responseText.append(
                            financeService.addFinanceOperation(
                                    getPreviousCommand(
                                            update.getMessage().getChatId()),
                                    update.getMessage().getText(),
                                    update.getMessage().getChatId()));
                }

                //Теперь мы сообщаем, что пора бы и ответ отправлять
                SendMessage response = SendMessage
                        .builder()
                        .chatId(update.getMessage().getChatId())
                        .text(responseText.toString())
                        .build();

                putPreviousCommand(update.getMessage().getChatId(), update.getMessage().getText());
                telegramClient.execute(response);

                if (activeChatRepository.findActiveChatByChatId(chatId).isEmpty()) {
                    ActiveChat activeChat = new ActiveChat();
                    activeChat.setChatId(chatId);
                    activeChatRepository.saveAndFlush(activeChat);
                    log.debug("Save new chat id {}", chatId);
                }
                //Ниже очень примитивная обработка исключений, чуть позже мы это поправим
            } catch (TelegramApiException e) {
                log.error("Возникла проблема соединения с ботом", e);
            } catch (Exception e) {
                log.error("Something went wrong with service", e);
            }
        }
    }

    public void sendNotificationToAllActiveChats(String message, Set<Long> chatIds) {
        for (Long id : chatIds) {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(id)
                    .text(message)
                    .build();
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Возникла проблема соединения с ботом", e);
            }
        }
    }

    private void putPreviousCommand(Long chatId, String command) {
        if (previousCommands.get(chatId) == null) {
            List<String> commands = new ArrayList<>();
            commands.add(command);
            previousCommands.put(chatId, commands);
        } else {
            previousCommands.get(chatId).add(command);
        }
    }

    private String getPreviousCommand(Long chatId) {
        return previousCommands.get(chatId)
                .get(previousCommands.get(chatId).size() - 1);
    }
}