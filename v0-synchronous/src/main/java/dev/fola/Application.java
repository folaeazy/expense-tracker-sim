package dev.fola;

import dev.fola.model.Expense;
import dev.fola.util.ExpenseParser;
import dev.fola.util.MockEmailGenerator;
import dev.fola.util.SummaryPrinter;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) {

        System.out.println("=== v0 - Synchronous Expense Importer ===\n");

        //Generate mock emails
        List<String> mockEmails = MockEmailGenerator.generateEmails(100);
        System.out.printf("Generated %d mock emails%n", mockEmails.size());

        //Parse them synchronously in a loop
        List<Expense> expenses = new ArrayList<>();
        int successCount = 0;

        for(String email : mockEmails) {
            Expense expense = ExpenseParser.emailParser(email);
            if(expense != null) {
                expenses.add(expense);
                successCount++;
                System.out.print(".");  // progress indicator
            }
        }
        System.out.println("\n\nParsing complete:");
        System.out.printf("✓ Successfully parsed: %d / %d%n", successCount, mockEmails.size());

        // summary printout with formatter
        SummaryPrinter.print(expenses);

        System.out.println("\n=== v0 completed successfully ===");



    }
}
