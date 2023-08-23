import budget.Budget;
import budget.BudgetRepo;

import java.util.List;

public class FakeBudgetRepo implements BudgetRepo {
    @Override
    public List<Budget> getAll() {
        return null;
    }
}
