package dev.fola.components;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ExpenseSpringSchedular {


    @Scheduled(fixedRate = 15000)
    @Async  //runs on virtual thread
    public void performAsync() {
        System.out.println("[" + Instant.now() + "] Starting scheduled sync on thread: " + Thread.currentThread());

        try {
            // Simulate work (later: real email fetch + parse)
            Thread.sleep(2000);
            System.out.println("[" + Instant.now() + "] Sync completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
