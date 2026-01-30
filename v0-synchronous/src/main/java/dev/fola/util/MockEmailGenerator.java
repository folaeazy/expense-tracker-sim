package dev.fola.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockEmailGenerator {

    private static final String[] CATEGORIES = {
            "food", "transport", "entertainment", "utilities", "shopping", "health", "travel"
    };

    private static final String[] MERCHANTS = {
            "Starbucks", "Uber", "Netflix", "IKEA", "Amazon", "Shell", "Tesla", "Apple Store",
            "Bolt", "Spotify", "Pharmacy", "Booking.com", "Zara"
    };

    private static final Random RANDOM = new Random();


    /**
     * Generate list of fake emails
     * @param count == how many fakes email to generate
     * @return List of emails String
     */

    public static List<String> generateEmails(int count) {
        List<String> emails = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double amount = 3.5 + RANDOM.nextDouble() * 120.0;  // between ~3.50 and ~123.50
            String merchant = MERCHANTS[RANDOM.nextInt(MERCHANTS.length)];
            String category = CATEGORIES[RANDOM.nextInt(CATEGORIES.length)];

            //Email style formats
            String[] formats = {
                    "You spent %.2f USD at %s #%s",
                    "Payment of $%.2f to %s - category: %s",
                    "%.2f paid via card at %s [%s]",
                    "Transaction: %.2f USD - %s - %s",
                    "Receipt: $%.2f was charged by %s (%s)"
            };
            String template = formats[RANDOM.nextInt(formats.length)];
            String emailBody = String.format(template, amount, merchant, category);

            emails.add(emailBody);
        }
        return emails;
    };

//    public static void main(String[] args) {
//        generateEmails(15).forEach(System.out::println);
//    }

}
