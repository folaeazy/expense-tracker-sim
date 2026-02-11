package dev.fola.components;

import dev.fola.model.Expense;
import dev.fola.util.ExpenseParser;
import dev.fola.util.MockEmailGenerator;
import dev.fola.util.SummaryPrinter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.err;

@Component
public class ExpenseSpringSchedular {
    private  final List<Expense> expenseList = new CopyOnWriteArrayList<>(); // thread safe , persist across runs


    @Scheduled(fixedRate = 30000)  //runs every 30 seconds
    @Async  //runs on virtual thread
    public void performAsync() {
        System.out.println("[" + Instant.now() + "] Starting scheduled sync on thread: " + Thread.currentThread());

        try {
            // Simulate work (later: real email fetch + parse)
            // simulate in batch per schedule
            int batchSize = 50;
            List<String> mockEmails = MockEmailGenerator.generateEmails(50);

            List<Expense> batchExpenses = new ArrayList<>();
            //parse to async / virtual thread
            for (String email : mockEmails) {
                Expense exp = ExpenseParser.emailParser(email);
                if(exp != null) batchExpenses.add(exp);

            }

            //accumulate all results
            expenseList.addAll(batchExpenses);

            System.out.println("Batch processed " + batchExpenses.size() + " expenses:");
            SummaryPrinter.print(batchExpenses);

            // Optional: summary of all time
            System.out.println("Total accumulated expenses so far: " + expenseList.size());
            SummaryPrinter.print(new ArrayList<>(expenseList)); // uncomment to see grand total

        } catch (Exception e){
            System.err.println("Error during scheduled sync: " + e.getMessage());
        }

        System.out.println("[" + Instant.now() + "] Scheduled sync completed");
    }
}
