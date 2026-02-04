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

        final int WARMUP_RUNS = 2;
        final int MEASUREMENT_RUNS = 5;
        final int EMAIL_COUNT = 5000;
        long totalNs = 0;

        List<Expense> expenses = new ArrayList<>();

        for(int run = 1;  run <= WARMUP_RUNS + MEASUREMENT_RUNS; run++) {

            //Generate mock emails
            List<String> mockEmails = MockEmailGenerator.generateEmails(EMAIL_COUNT);
            System.out.printf("Generated %d mock emails%n", mockEmails.size());
            long start = System.nanoTime();

            //Parse them synchronously in a loop

            for(String email : mockEmails) {
                Expense expense = ExpenseParser.emailParser(email);
                if(expense != null) {
                    expenses.add(expense);
                    System.out.print(".");  // progress indicator
                }
            }
            long end = System.nanoTime();

            if (run > WARMUP_RUNS) {
                long durationNs = end - start;
                totalNs += durationNs;
                System.out.printf("Run %d: %.1f ms%n", run - WARMUP_RUNS, durationNs / 1_000_000.0);
            }

        }
        double avgMs = (totalNs / (double) MEASUREMENT_RUNS) / 1_000_000.0;
        System.out.printf("\nAverage over %d runs (%,d emails): %.1f ms%n", MEASUREMENT_RUNS, EMAIL_COUNT, avgMs);
        System.out.printf("Throughput: ~%.0f emails/sec%n", (EMAIL_COUNT / (avgMs / 1000.0)));




        // summary printout with formatter
        SummaryPrinter.print(expenses);

        System.out.println("\n=== v0 completed successfully ===");



    }
}
