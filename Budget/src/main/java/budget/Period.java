package budget;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public record Period(LocalDate start, LocalDate end) {
    public long overlappingDays(Period another) {
        if (start.isAfter(another.end) || end.isBefore(another.start)) {
            return 0;
        }
        LocalDate overlappingStart = start().isAfter(another.start)
                ? start()
                : another.start;
        LocalDate overlappingEnd = end().isBefore(another.end)
                ? end()
                : another.end;
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}