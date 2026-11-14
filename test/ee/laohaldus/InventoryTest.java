package ee.laohaldus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Käivitatav ilma välise testiraamistikuta; kontrollid ei sõltu -ea lipust. */
public class InventoryTest {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        Product milk = new FoodProduct("Piim", new BigDecimal("1.25"), 10,
                LocalDate.of(2027, 1, 1));
        inventory.addProduct(milk);
        inventory.addProduct(new ElectronicProduct("Hiir", new BigDecimal("19.90"), 2, 24));
        check(inventory.getTotalValue().equals(new BigDecimal("52.30")), "Täpne koguväärtus");
        check(inventory.searchByName(" PI ").equals(java.util.List.of(milk)), "Osaline otsing");
        check(inventory.searchByName("puuduv").isEmpty(), "Puuduv vaste");
        inventory.changeQuantity("PIIM", -3);
        inventory.changeQuantity("Piim", 1);
        check(milk.getQuantity() == 8, "Koguse vähendamine ja suurendamine");
        rejects(() -> inventory.changeQuantity("Piim", -9));
        check(milk.getQuantity() == 8, "Vigane muudatus ei muuda kogust");
        rejects(() -> inventory.changeQuantity("puuduv", 1));
        rejects(() -> inventory.addProduct(new ElectronicProduct("piim", BigDecimal.ONE, 1, 0)));
        rejects(() -> inventory.searchByName(" "));
        rejects(() -> milk.setPrice(new BigDecimal("-0.01")));
        rejects(() -> new ElectronicProduct("", BigDecimal.ONE, 0, 0));
        rejects(() -> new ElectronicProduct("x", BigDecimal.ONE, -1, 0));
        rejects(() -> new ElectronicProduct("x", BigDecimal.ONE, 0, -1));
        rejects(() -> new ElectronicProduct("x", new BigDecimal("1.001"), 0, 0));
        Product large = new ElectronicProduct("Suur", BigDecimal.ONE, Integer.MAX_VALUE, 0);
        rejects(() -> large.changeQuantity(1));
        try {
            inventory.getProducts().clear();
            throw new AssertionError("Toodete loend peab olema muutmatu");
        } catch (UnsupportedOperationException expected) {
            // Loendi struktuuri saab muuta ainult Inventory kaudu.
        }
        check(milk.getAdditionalInfo().contains("parim enne"), "Toidukauba polümorfism");
        check(inventory.getProducts().get(1).getAdditionalInfo().contains("garantii"),
                "Elektroonika polümorfism");
        System.out.println("Kõik laohalduse testid läbisid edukalt.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | ArithmeticException expected) {
            return;
        }
        throw new AssertionError("Vigane sisend peab olema tagasi lükatud");
    }
}
