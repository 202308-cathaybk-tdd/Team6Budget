package budget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Budget {
    String yearMonth;

    BigDecimal amount;

    public Budget(String yearMonth, BigDecimal amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public BigDecimal overlappingAmount(Period period) {
        long days = period.overlappingDays(new Period(firstDay(), lastDay()));
        return getDailyAmount().multiply(BigDecimal.valueOf(days));
    }

    private String getYearMonth() {
        return yearMonth;
    }

    private BigDecimal getAmount() {
        return amount;
    }

    private BigDecimal getDailyAmount() {
        return getAmount().divide(new BigDecimal(getYearMonthInstance().lengthOfMonth()), 0, RoundingMode.HALF_UP);
    }

    private LocalDate lastDay() {
        return getYearMonthInstance().atEndOfMonth();
    }

    private LocalDate firstDay() {
        YearMonth budgetYearMonth = getYearMonthInstance();
        return budgetYearMonth.atDay(1);
    }

    private YearMonth getYearMonthInstance() {
        return YearMonth.parse(getYearMonth(), DateTimeFormatter.ofPattern("yyyyMM"));
    }
}
