package budget;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public record Period(LocalDate start, LocalDate end) {
    public BigDecimal overlappingDays(Budget budget) {
        LocalDate overlappingStart = start().isAfter(budget.firstDay())
                ? start()
                : budget.firstDay();
        LocalDate overlappingEnd = end().isBefore(budget.lastDay())
                ? end()
                : budget.lastDay();
        return new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
    }
}