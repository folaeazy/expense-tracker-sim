package dev.fola.util;


import dev.fola.model.Expense;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a mock email body string into an Expense object.
 * Returns null if parsing fails (invalid format, missing data, etc.).
 */
public class ExpenseParser {


    //Regex to find dollar amount
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?:\\$|USD\\s*|Paid\\s*|spent\\s*|Transaction:\\s*|charged\\s*|you\\s*)?(\\d+\\.?\\d{0,2})");

    /**
     *
     * @param emailBody from email content
     * @return expense object or null if parsing fails
     */
    public static Expense emailParser(String emailBody) {
        if (emailBody == null || emailBody.trim().isEmpty()) {
            return null;
        }

        // Step 1: Try to find the amount
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(emailBody);
        if (!amountMatcher.find()) {
            return null; // No recognizable amount
        }


        double amount;
        try {
            amount = Double.parseDouble(amountMatcher.group(1));
        } catch (NumberFormatException e) {
            return null;
        }

        // Step 2: Extract category (look for #category at the end or known words)
        String category = "other"; // default
        String lower = emailBody.toLowerCase();

        if (lower.contains("#food") || lower.contains("groceries") || lower.contains("restaurant")) {
            category = "food";
        } else if (lower.contains("#transport") || lower.contains("uber") || lower.contains("taxi") || lower.contains("ride")) {
            category = "transport";
        } else if (lower.contains("#entertainment") || lower.contains("movie") || lower.contains("netflix") || lower.contains("spotify")) {
            category = "entertainment";
        } else if (lower.contains("#utilities") || lower.contains("electricity") || lower.contains("bill")) {
            category = "utilities";
        } else if (lower.contains("#shopping") || lower.contains("amazon") || lower.contains("zara") || lower.contains("ikea")) {
            category = "shopping";
        } else if (lower.contains("#health") || lower.contains("pharmacy")) {
            category = "health";
        } else if (lower.contains("#travel") || lower.contains("booking.com")) {
            category = "travel";
        }

        // Step 3: Use the whole string as description (trimmed)
        String description = emailBody.trim();
        // Optional: clean up a bit
        description = description.replaceAll("\\s+#\\w+\\b", ""); // remove #category if present

        // Simulate realistic work: AI model inference latency or network/API delay
        try {
            Thread.sleep(2 + new java.util.Random().nextInt(10));  // 2–11 ms per email
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new Expense(amount, category, description, Instant.now());
    }

    //For testing purpose

//    public static void main(String[] args) {
//        MockEmailGenerator.generateEmails(5).forEach(email -> {
//            Expense exp = emailParser(email);
//            if (exp != null) {
//                System.out.printf("Parsed: %.2f | %s | %s | %s%n",
//                        exp.amount(), exp.category(), exp.description(), exp.timestamp());
//            } else {
//                System.out.println("Failed to parse: " + email);
//            }
//        });
//    }
}
