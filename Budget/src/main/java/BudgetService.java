import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class BudgetService {

    private final BudgetRepo budgetRepo;

    public BudgetService(BudgetRepo budgetRepo) {
        this.budgetRepo = budgetRepo;
    }

    public BigDecimal totalAmount(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            return BigDecimal.ZERO;
        }

        List<Budget> budgetList = budgetRepo.getAll();

        YearMonth startYearMonth = YearMonth.from(start);
        YearMonth endYearMonth = YearMonth.from(end);

        return budgetList.stream().filter(budget -> {
            YearMonth yearMonth = YearMonth.parse(budget.getYearMonth(), DateTimeFormatter.ofPattern("yyyyMM"));
            return yearMonth.compareTo(startYearMonth) >= 0 && yearMonth.compareTo(endYearMonth) <= 0;
        }).map(budget -> {
            YearMonth yearMonth = budget.getYearMonthInstance();

            int dayOfMonth = yearMonth.lengthOfMonth();
            BigDecimal days;
            if (startYearMonth.equals(endYearMonth)) {
                LocalDate overlappingStart = start;
                LocalDate overlappingEnd = end;
//                days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
            } else if (yearMonth.equals(startYearMonth)) {
                LocalDate overlappingStart = start;
                LocalDate overlappingEnd = yearMonth.atEndOfMonth();
//                days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
            } else if (yearMonth.equals(endYearMonth)) {
                LocalDate overlappingStart = yearMonth.atDay(1);
                LocalDate overlappingEnd = end;
//                days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
            } else {
                LocalDate overlappingStart = yearMonth.atDay(1);
                LocalDate overlappingEnd = yearMonth.atEndOfMonth();
//                days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
            }
            days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
            BigDecimal dailyAmount = budget.getAmount().divide(new BigDecimal(dayOfMonth), 0, RoundingMode.HALF_UP);
            return dailyAmount.multiply(days);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
