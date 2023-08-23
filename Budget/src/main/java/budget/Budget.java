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

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    BigDecimal getDailyAmount() {
        return getAmount().divide(new BigDecimal(getYearMonthInstance().lengthOfMonth()), 0, RoundingMode.HALF_UP);
    }

    LocalDate lastDay() {
        return getYearMonthInstance().atEndOfMonth();
    }

    LocalDate firstDay() {
        YearMonth budgetYearMonth = getYearMonthInstance();
        return budgetYearMonth.atDay(1);
    }

    YearMonth getYearMonthInstance() {
        return YearMonth.parse(getYearMonth(), DateTimeFormatter.ofPattern("yyyyMM"));
    }
}
