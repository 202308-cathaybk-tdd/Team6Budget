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

    private static BigDecimal overlappingDays(LocalDate start, LocalDate end, Budget budget) {
        LocalDate overlappingStart;
        LocalDate overlappingEnd;
        if (YearMonth.from(start).equals(YearMonth.from(end))) {
            overlappingStart = start;
            overlappingEnd = end;
        } else if (budget.getYearMonthInstance().equals(YearMonth.from(start))) {
            overlappingStart = start;
            overlappingEnd = budget.getYearMonthInstance().atEndOfMonth();
        } else if (budget.getYearMonthInstance().equals(YearMonth.from(end))) {
            overlappingStart = budget.getYearMonthInstance().atDay(1);
            overlappingEnd = end;
        } else {
            overlappingStart = budget.getYearMonthInstance().atDay(1);
            overlappingEnd = budget.getYearMonthInstance().atEndOfMonth();
        }
        return new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
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

            BigDecimal days = overlappingDays(start, end, budget);
            BigDecimal dailyAmount = budget.getAmount().divide(new BigDecimal(budget.getYearMonthInstance().lengthOfMonth()), 0, RoundingMode.HALF_UP);
            return dailyAmount.multiply(days);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
