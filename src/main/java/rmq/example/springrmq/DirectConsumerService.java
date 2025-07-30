// package rmq.example.springrmq;

// import com.rabbitmq.client.*;
// import org.springframework.stereotype.Service;

// import jakarta.annotation.PostConstruct;
// import jakarta.annotation.PreDestroy;

// import java.nio.charset.StandardCharsets;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

// @Service
// public class DirectConsumerService {

//     private Connection connection;
//     private Channel classicChannel;
//     private Channel quorumChannel;
//     private Channel streamChannel;
//     private final ExecutorService executor = Executors.newFixedThreadPool(3);

//     @PostConstruct
//     public void init() throws Exception {
//         ConnectionFactory factory = new ConnectionFactory();
//         factory.setHost("localhost");
//         factory.setPort(5672);
//         factory.setUsername("arul");
//         factory.setPassword("password");
//         connection = factory.newConnection();

//         classicChannel = connection.createChannel();
//         quorumChannel = connection.createChannel();
//         streamChannel = connection.createChannel();

//         startConsumer(classicChannel, "classic.transactions");
//         startConsumer(quorumChannel, "quorum.transactions");
//         startConsumer(streamChannel, "stream.transactions");
//     }

//     private void startConsumer(Channel channel, String queueName) {
//         executor.submit(() -> {
//             while (true) {
//                 try {
//                     GetResponse response;
//                     int count = 0;
//                     while (count < 10 && (response = channel.basicGet(queueName, false)) != null) {
//                         String body = new String(response.getBody(), StandardCharsets.UTF_8);
//                         // You can process the message here
//                         channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
//                         count++;
//                     }
//                     Thread.sleep(1000); // Wait 1 second before next batch
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }
//         });
//     }

//     @PreDestroy
//     public void cleanup() throws Exception {
//         if (classicChannel != null) classicChannel.close();
//         if (quorumChannel != null) quorumChannel.close();
//         if (streamChannel != null) streamChannel.close();
//         if (connection != null) connection.close();
//         executor.shutdownNow();
//     }
// }