package ru.skillfactory.homework.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillfactory.homework.telegrambot.entity.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
}
