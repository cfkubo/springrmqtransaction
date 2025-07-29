package rmq.example.springrmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DirectPublisherService {

    private Connection connection;
    private Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
    private final List<TransactionRecord> publishedRecords = new CopyOnWriteArrayList<>();
    private final AtomicLong confirmCount = new AtomicLong();
    private final Map<String, AtomicLong> publishedCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> confirmedCount = new ConcurrentHashMap<>();

    public List<TransactionRecord> getPublishedRecords() {
        return publishedRecords;
    }
    public long getConfirmCount() {
        return confirmCount.get();
    }
    public long getPublishedCount(String queue) {
        return publishedCount.getOrDefault(queue, new AtomicLong(0)).get();
    }
    public long getConfirmedCount(String queue) {
        return confirmedCount.getOrDefault(queue, new AtomicLong(0)).get();
    }

    // Helper class to track transaction and queue
    public static class TransactionRecord {
        public final Transaction transaction;
        public final String queue;
        public TransactionRecord(Transaction transaction, String queue) {
            this.transaction = transaction;
            this.queue = queue;
        }
    }

    private void publishWithConfirm(String queue, String body, AMQP.BasicProperties props) throws Exception {
        long seqNo = channel.getNextPublishSeqNo();
        outstandingConfirms.put(seqNo, queue);
        channel.basicPublish("", queue, props, body.getBytes());
        publishedCount.computeIfAbsent(queue, k -> new AtomicLong()).incrementAndGet();
    }

    @PostConstruct
    public void init() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("arul");
        factory.setPassword("password");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.confirmSelect();

        // Declare queues (classic, quorum, stream)
        channel.queueDeclare("classic.transactions", true, false, false, null);
        channel.queueDeclare("quorum.transactions", true, false, false,
                Map.of("x-queue-type", "quorum"));
        channel.queueDeclare("stream.transactions", true, false, false,
                Map.of("x-queue-type", "stream"));

        // Confirm listeners
        ConfirmCallback ackCallback = (seqNo, multiple) -> {
            if (multiple) {
                outstandingConfirms.headMap(seqNo, true).forEach((k, queue) -> {
                    confirmedCount.computeIfAbsent(queue, q -> new AtomicLong()).incrementAndGet();
                });
                outstandingConfirms.headMap(seqNo, true).clear();
            } else {
                String queue = outstandingConfirms.get(seqNo);
                confirmedCount.computeIfAbsent(queue, q -> new AtomicLong()).incrementAndGet();
                outstandingConfirms.remove(seqNo);
            }
        };
        ConfirmCallback nackCallback = (seqNo, multiple) -> {
            String body = outstandingConfirms.get(seqNo);
            System.err.printf("Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                    body, seqNo, multiple);
            if (multiple) {
                outstandingConfirms.headMap(seqNo, true).clear();
            } else {
                outstandingConfirms.remove(seqNo);
            }
        };
        channel.addConfirmListener(ackCallback, nackCallback);
    }

    public void publishTransaction(Transaction tx) {
        try {
            String json = objectMapper.writeValueAsString(tx);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .deliveryMode(2)
                    .build();

            // Publish to all three queues
            publishWithConfirm("classic.transactions", json, props);
            publishWithConfirm("quorum.transactions", json, props);
            publishWithConfirm("stream.transactions", json, props);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanup() throws Exception {
        if (channel != null) channel.close();
        if (connection != null) connection.close();
    }
}