package ee.laohaldus;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Kõigi tooteliikide ühised andmed ja käitumine. */
public abstract class Product {
    private final String name;
    private BigDecimal price;
    private int quantity;

    protected Product(String name, BigDecimal price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Toote nimi ei tohi olla tühi.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Kogus ei tohi olla negatiivne.");
        }
        this.name = name.strip();
        setPrice(price);
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Hind ei tohi olla negatiivne ega puududa.");
        }
        this.price = price.setScale(2, RoundingMode.UNNECESSARY);
    }

    public void changeQuantity(int change) {
        long result = (long) quantity + change;
        if (result < 0 || result > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Uus kogus peab olema vahemikus 0 kuni "
                    + Integer.MAX_VALUE + ".");
        }
        quantity = (int) result;
    }

    public BigDecimal getStockValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public abstract String getAdditionalInfo();

    @Override
    public String toString() {
        return name + " | hind: " + price.toPlainString() + " EUR | kogus: "
                + quantity + " | " + getAdditionalInfo();
    }
}
