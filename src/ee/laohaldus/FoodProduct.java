package ee.laohaldus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class FoodProduct extends Product {
    private final LocalDate bestBefore;

    public FoodProduct(String name, BigDecimal price, int quantity, LocalDate bestBefore) {
        super(name, price, quantity);
        this.bestBefore = Objects.requireNonNull(bestBefore, "Säilivuskuupäev puudub.");
    }

    public LocalDate getBestBefore() { return bestBefore; }

    @Override
    public String getAdditionalInfo() {
        return "toidukaup, parim enne: " + bestBefore;
    }
}
