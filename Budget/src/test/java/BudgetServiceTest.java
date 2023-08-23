import budget.Budget;
import budget.BudgetRepo;
import budget.BudgetService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BudgetServiceTest {


    private BudgetRepo budgetRepo;

    private BudgetService budgetService;

    @Before
    public void setUp() throws Exception {
        budgetRepo = mock(BudgetRepo.class);
        budgetService = new BudgetService(budgetRepo);
    }

    @Test
    public void invalidDateRage() {
        assertEquals(BigDecimal.ZERO, budgetService.totalAmount(LocalDate.of(2023, 1, 2), LocalDate.of(2021, 1, 1)));
    }

    @Test
    public void fullMonthDateRange() {
        when(budgetRepo.getAll()).thenReturn(Arrays.asList(new Budget("202301", new BigDecimal(31))));
        assertEquals(new BigDecimal(31), budgetService.totalAmount(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31)));
    }

    @Test
    public void oneDayRange() {
        when(budgetRepo.getAll()).thenReturn(Arrays.asList(new Budget("202301", new BigDecimal(31))));
        assertEquals(new BigDecimal(1), budgetService.totalAmount(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1)));
    }

    @Test
    public void crossMonthRange() {
        when(budgetRepo.getAll()).thenReturn(Arrays.asList(new Budget("202303", new BigDecimal(31)), new Budget("202304", new BigDecimal(300))));
        assertEquals(new BigDecimal(11), budgetService.totalAmount(LocalDate.of(2023, 3, 31), LocalDate.of(2023, 4, 1)));
    }

    @Test
    public void crossThreeMonthRange() {
        when(budgetRepo.getAll()).thenReturn(Arrays.asList(
                new Budget("202303", new BigDecimal(31)),
                new Budget("202304", new BigDecimal(300)),
                new Budget("202305", new BigDecimal(3100))));
        assertEquals(new BigDecimal(401), budgetService.totalAmount(LocalDate.of(2023, 3, 31), LocalDate.of(2023, 5, 1)));
    }

    @Test
    public void crossNoDataMonthRange() {
        when(budgetRepo.getAll()).thenReturn(Arrays.asList(
                new Budget("202303", new BigDecimal(31)),
                new Budget("202305", new BigDecimal(3100))));
        assertEquals(new BigDecimal(101), budgetService.totalAmount(LocalDate.of(2023, 3, 31), LocalDate.of(2023, 5, 1)));
    }

    @Test
    public void crossAdditionalMonthData() {
        when(budgetRepo.getAll()).thenReturn(Arrays.asList(
                new Budget("202303", new BigDecimal(31)),
                new Budget("202304", new BigDecimal(300)),
                new Budget("202305", new BigDecimal(3100)),
                new Budget("202306", new BigDecimal(30000))));
        assertEquals(new BigDecimal(401), budgetService.totalAmount(LocalDate.of(2023, 3, 31), LocalDate.of(2023, 5, 1)));
    }
}
