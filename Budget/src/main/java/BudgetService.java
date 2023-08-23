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
            LocalDate overlappingStart;
            LocalDate overlappingEnd;
            if (startYearMonth.equals(endYearMonth)) {
                overlappingStart = start;
                overlappingEnd = end;
            } else if (yearMonth.equals(startYearMonth)) {
                overlappingStart = start;
                overlappingEnd = yearMonth.atEndOfMonth();
            } else if (yearMonth.equals(endYearMonth)) {
                overlappingStart = yearMonth.atDay(1);
                overlappingEnd = end;
            } else {
                overlappingStart = yearMonth.atDay(1);
                overlappingEnd = yearMonth.atEndOfMonth();
            }
            BigDecimal days = new BigDecimal(DAYS.between(overlappingStart, overlappingEnd) + 1);
            BigDecimal dailyAmount = budget.getAmount().divide(new BigDecimal(dayOfMonth), 0, RoundingMode.HALF_UP);
            return dailyAmount.multiply(days);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
