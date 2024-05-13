package ru.skillfactory.homework.telegrambot.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillfactory.homework.telegrambot.dto.cb.ValuteCursOnDate;
import ru.skillfactory.homework.telegrambot.service.CentralRussianBankService;

import java.util.List;

@RestController
@AllArgsConstructor
public class CurrencyController {

    private final CentralRussianBankService centralRussianBankService;

    @GetMapping("/getCurrencies")
    public List<ValuteCursOnDate> getValuteCursOnDate() throws Exception {
        return centralRussianBankService.getCurrenciesFromCbr();
    }
}