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
        YearMonth endYearMonth = YearMonth.from(end);
        YearMonth startYearMonth = YearMonth.from(start);
        LocalDate overlappingStart;
        LocalDate overlappingEnd;
        if (startYearMonth.equals(endYearMonth)) {
            overlappingStart = start;
            overlappingEnd = end;
        } else if (budget.getYearMonthInstance().equals(startYearMonth)) {
            overlappingStart = start;
            overlappingEnd = budget.getYearMonthInstance().atEndOfMonth();
        } else if (budget.getYearMonthInstance().equals(endYearMonth)) {
            overlappingStart = budget.getYearMonthInstance().atDay(1);
            overlappingEnd = end;
        } else {
            overlappingStart = budget.getYearMonthInstance().atDay(1);
            overlappingEnd = budget.getYearMonthInstance().atEndOfMonth();
        }
        BigDecimal days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
        return days;
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
