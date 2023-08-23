package budget;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public record Period(LocalDate start, LocalDate end) {
    public long overlappingDays(Budget budget) {
        Period another = new Period(budget.firstDay(), budget.lastDay());
        LocalDate overlappingStart = start().isAfter(another.start)
                ? start()
                : another.start;
        LocalDate overlappingEnd = end().isBefore(another.end)
                ? end()
                : another.end;
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}