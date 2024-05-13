package ru.skillfactory.homework.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillfactory.homework.telegrambot.entity.Income;
import ru.skillfactory.homework.telegrambot.entity.Spend;
import ru.skillfactory.homework.telegrambot.repository.IncomeRepository;
import ru.skillfactory.homework.telegrambot.repository.SpendRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private static final String ADD_INCOME = "/addincome";

    private final IncomeRepository incomeRepository;
    private final SpendRepository spendRepository;


    public String addFinanceOperation(String operationType, String price, Long chatId) {
        String message;
        if (ADD_INCOME.equalsIgnoreCase(operationType)) {
            Income income = new Income();
            income.setChatId(chatId);
            income.setIncome(new BigDecimal(price));
            incomeRepository.saveAndFlush(income);
            message = "Доход в размере " + price + " был успешно добавлен";
        } else {
            Spend spend = new Spend();
            spend.setChatId(chatId);
            spend.setSpend(new BigDecimal(price));
            spendRepository.saveAndFlush(spend);
            message = "Расход в размере " + price + " был успешно добавлен";
        }
        return message;
    }
}
