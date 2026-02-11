package dev.fola.util;

import dev.fola.model.Expense;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SummaryPrinter {

    /**
     * Prints a summary report of the expenses:
     * - Total count
     * - Grand total amount
     * - Sum per category (sorted by amount descending)
     *
     * @param expenses list of parsed Expense objects
     */

    public static  void print(List<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            System.out.println("No expenses found");
            return;
        }

        // Get the total for each category
        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(Expense::category,
                        Collectors.summingDouble(Expense::amount)
                        ));


        // grand total
        double grandTotal = categoryTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        int totalCount = expenses.size();

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.printf("║ Expense Summary - %d items processed      ║\n", totalCount);
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.printf("║ Grand Total:               $%10.2f     ║\n", grandTotal);
        System.out.println("╠════════════════════╦═══════════════════════╣");
        System.out.println("║ Category           ║ Amount                ║");
        System.out.println("╠════════════════════╬═══════════════════════╣");


        categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    System.out.printf("║ %-18s ║ $%10.2f          ║\n",
                            entry.getKey(),
                            entry.getValue());
                });

    }
}
