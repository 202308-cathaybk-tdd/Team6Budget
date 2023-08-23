package budget;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public record Period(LocalDate start, LocalDate end) {
    public long overlappingDays(Budget budget) {
        LocalDate overlappingStart = start().isAfter(budget.firstDay())
                ? start()
                : budget.firstDay();
        LocalDate overlappingEnd = end().isBefore(budget.lastDay())
                ? end()
                : budget.lastDay();
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}