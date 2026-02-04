package dev.fola.model;

import java.time.Instant;

public record Expense(
        double amount,
        String category,
        String description,
        Instant timestamp
) {
}
