package rmq.example.springrmq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class TransactionSimulatorRunner implements CommandLineRunner {

    private final DirectPublisherService publisherService;
    private final Random random = new Random();

    public TransactionSimulatorRunner(DirectPublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            Transaction tx = new Transaction();
            tx.setTransactionId(UUID.randomUUID().toString());
            tx.setAccountNumber("CHK" + (10000 + random.nextInt(90000)));
            tx.setAmount(BigDecimal.valueOf(random.nextDouble() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP));
            tx.setType(random.nextBoolean() ? "DEPOSIT" : "WITHDRAWAL");
            tx.setTimestamp(LocalDateTime.now());
            publisherService.publishTransaction(tx);
            // Thread.sleep(1); // 1 second interval
        }
    }
}