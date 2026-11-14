package ee.laohaldus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Inventory {
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        Objects.requireNonNull(product, "Toode puudub.");
        if (products.stream().anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()))) {
            throw new IllegalArgumentException("Selle nimega toode on juba olemas.");
        }
        products.add(product);
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public List<Product> searchByName(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Otsingusõna ei tohi olla tühi.");
        }
        String normalized = query.strip().toLowerCase(Locale.ROOT);
        List<Product> matches = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase(Locale.ROOT).contains(normalized)) {
                matches.add(product);
            }
        }
        return List.copyOf(matches);
    }

    public void changeQuantity(String name, int change) {
        Product product = products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name.strip()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sellise nimega toodet ei leitud."));
        product.changeQuantity(change);
    }

    public BigDecimal getTotalValue() {
        BigDecimal total = new BigDecimal("0.00");
        for (Product product : products) {
            total = total.add(product.getStockValue());
        }
        return total;
    }
}
