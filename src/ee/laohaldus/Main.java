package ee.laohaldus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Inventory inventory = new Inventory();
    private final Scanner input;

    public Main(Scanner input) {
        this.input = input;
        inventory.addProduct(new FoodProduct("Piim", new BigDecimal("1.25"), 10,
                LocalDate.now().plusDays(7)));
        inventory.addProduct(new FoodProduct("Leib", new BigDecimal("2.10"), 8,
                LocalDate.now().plusDays(5)));
        inventory.addProduct(new ElectronicProduct("Hiir", new BigDecimal("19.90"), 3, 24));
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8)) {
            new Main(scanner).run();
        }
    }

    public void run() {
        System.out.println("VÄIKESE POE LAOHALDUS");
        try {
            while (true) {
                System.out.println("\n1 – Kõik tooted\n2 – Lisa toode\n3 – Muuda kogust"
                        + "\n4 – Otsi nime järgi\n5 – Lao koguväärtus\n0 – Välju");
                String choice = read("Valik: ");
                try {
                    switch (choice) {
                        case "1" -> printProducts(inventory.getProducts());
                        case "2" -> addProduct();
                        case "3" -> changeQuantity();
                        case "4" -> printProducts(inventory.searchByName(read("Otsingusõna: ")));
                        case "5" -> System.out.println("Lao koguväärtus: "
                                + inventory.getTotalValue().toPlainString() + " EUR");
                        case "0" -> { System.out.println("Head aega!"); return; }
                        default -> System.out.println("Tundmatu valik. Vali 0–5.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Viga: sisesta sobivas vahemikus arv, hind kuni kahe komakohaga.");
                } catch (DateTimeParseException exception) {
                    System.out.println("Viga: sisesta olemasolev kuupäev kujul AAAA-KK-PP.");
                } catch (ArithmeticException exception) {
                    System.out.println("Viga: hinnal võib olla kuni kaks komakohta.");
                } catch (IllegalArgumentException exception) {
                    System.out.println("Viga: " + exception.getMessage());
                }
            }
        } catch (EndOfInput exception) {
            System.out.println("\nSisend lõppes. Head aega!");
        }
    }

    private void addProduct() {
        String type = read("Liik (1 – toit, 2 – elektroonika): ");
        if (!type.equals("1") && !type.equals("2")) {
            throw new IllegalArgumentException("Tooteliik peab olema 1 või 2.");
        }
        String name = read("Nimi: ");
        BigDecimal price = new BigDecimal(read("Hind EUR: ").replace(',', '.'));
        int quantity = readInt("Kogus: ");
        Product product;
        if (type.equals("1")) {
            LocalDate date = LocalDate.parse(read("Parim enne (AAAA-KK-PP): "));
            product = new FoodProduct(name, price, quantity, date);
        } else {
            product = new ElectronicProduct(name, price, quantity, readInt("Garantii kuudes: "));
        }
        inventory.addProduct(product);
        System.out.println("Toode lisatud: " + product);
    }

    private void changeQuantity() {
        String name = read("Toote täpne nimi: ");
        int change = readInt("Koguse muutus (nt 5 või -2): ");
        inventory.changeQuantity(name, change);
        System.out.println("Kogus muudetud.");
    }

    private void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Tooteid ei leitud.");
        }
        for (Product product : products) {
            System.out.println(product);
        }
    }

    private int readInt(String prompt) { return Integer.parseInt(read(prompt)); }

    private String read(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!input.hasNextLine()) throw new EndOfInput();
        return input.nextLine().strip();
    }

    private static class EndOfInput extends RuntimeException { }
}
