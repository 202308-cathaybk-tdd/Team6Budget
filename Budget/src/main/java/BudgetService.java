import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            YearMonth yearMonth = YearMonth.parse(budget.getYearMonth(), DateTimeFormatter.ofPattern("yyyyMM"));

            if (!yearMonth.equals(startYearMonth) && !yearMonth.equals(endYearMonth)) {
                return budget.getAmount();
            }

            int dayOfMonth = yearMonth.atEndOfMonth().getDayOfMonth();
            BigDecimal dailyAmount = budget.getAmount().divide(new BigDecimal(dayOfMonth), 2, RoundingMode.HALF_UP);

            if (yearMonth.equals(startYearMonth)) {
                BigDecimal days;

                if (startYearMonth.equals(endYearMonth)) {
                    days = new BigDecimal(end.getDayOfMonth() - start.getDayOfMonth() + 1);
                } else {
                    days = new BigDecimal(dayOfMonth - start.getDayOfMonth() + 1);
                }

                return dailyAmount.multiply(days);
            }

            return dailyAmount.multiply(new BigDecimal(end.getDayOfMonth()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
