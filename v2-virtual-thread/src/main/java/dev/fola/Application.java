package dev.fola;

import dev.fola.model.Expense;
import dev.fola.util.ExpenseParser;
import dev.fola.util.MockEmailGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * v1 - Basic Threads Expense Importer with fair timing comparison
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("=== v2 - Virtual Thread   Importer (with timing loop) ===\n");

        // === Configuration - tune these to see clear differences ===
        final int EMAIL_COUNT       = 20000;       // Try 1000, 5000, 20000...
        final int WARMUP_RUNS       = 3;          // Enough to warm up JIT
        final int MEASUREMENT_RUNS  = 5;          // For stable average


        long totalDurationNs = 0;
        long totalProcessed  = 0;

        System.out.printf("Benchmark settings: %,d emails × %d measured runs (after %d warmup)%n",
                EMAIL_COUNT, MEASUREMENT_RUNS, WARMUP_RUNS);


        AtomicLong taskCount = new AtomicLong(0);

        for (int run = 1; run <= WARMUP_RUNS + MEASUREMENT_RUNS; run++) {
            boolean isMeasured = run > WARMUP_RUNS;

            // Fresh data each run
            List<String> mockEmails = MockEmailGenerator.generateEmails(EMAIL_COUNT);

            List<Expense> expenses = new CopyOnWriteArrayList<>();

            //create thread pool
            ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

            //submit task
            List<Future<Expense>> futures = new ArrayList<>();
            long startNs = System.nanoTime();


            for (String email : mockEmails) {
                Future<Expense> future = executorService.submit(()-> {
                    taskCount.incrementAndGet();               // ← count executed tasks
                    System.out.println("Task #" + taskCount.get() + " on " + Thread.currentThread());
                    return ExpenseParser.emailParser(email);
                });
                futures.add(future);
            }

            // Wait for completion
            for (Future<Expense> future : futures) {
                try {
                    Expense expense = future.get();
                    if(expense != null) expenses.add(expense);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            long endNs = System.nanoTime();
            long durationNs = endNs - startNs;
            //clean service
            executorService.shutdown();

            if (isMeasured) {
                totalDurationNs += durationNs;
                totalProcessed += expenses.size();

                double ms = durationNs / 1_000_000.0;
                System.out.printf("Run %2d (measured): %8.1f ms   |  %,d expenses%n",
                        run - WARMUP_RUNS, ms, expenses.size());
            } else {
                System.out.printf("Run %2d (warmup)  : %8.1f ms%n",
                        run, durationNs / 1_000_000.0);
            }
        }
        System.out.println("Total tasks executed: " + taskCount.get());

        // Final stats
        if (MEASUREMENT_RUNS > 0) {
            double avgMs = (totalDurationNs / (double) MEASUREMENT_RUNS) / 1_000_000.0;
            double avgSec = avgMs / 1000.0;
            double throughput = EMAIL_COUNT / avgSec;

            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║              Final Benchmark               ║");
            System.out.println("╠════════════════════════════════════════════╣");
            System.out.printf("║ Avg time (%,d runs)     : %8.1f ms      ║%n", MEASUREMENT_RUNS, avgMs);
            System.out.printf("║ Avg time                : %8.2f s       ║%n", avgSec);
            System.out.printf("║ Throughput              : %8.0f emails/s ║%n", throughput);
            System.out.println("╚════════════════════════════════════════════╝");

        }

        // Optional: print one summary (from last run) - comment out for pure perf testing
        // SummaryPrinter.print(expenses);
    }
}