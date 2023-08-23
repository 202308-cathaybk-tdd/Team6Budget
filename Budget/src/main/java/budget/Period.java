package budget;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public record Period(LocalDate start, LocalDate end) {
    public long overlappingDays(Budget budget) {
        Period another = new Period(budget.firstDay(), budget.lastDay());
        LocalDate firstDay = budget.firstDay();
        LocalDate lastDay = budget.lastDay();
        LocalDate overlappingStart = start().isAfter(firstDay)
                ? start()
                : firstDay;
        LocalDate overlappingEnd = end().isBefore(lastDay)
                ? end()
                : lastDay;
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}