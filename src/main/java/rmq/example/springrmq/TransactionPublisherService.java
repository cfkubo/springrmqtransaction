package rmq.example.springrmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TransactionPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransactionPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Async
    public void publishTransaction(rmq.example.springrmq.Transaction tx) {
        try {
            String json = objectMapper.writeValueAsString(tx);
            rabbitTemplate.convertAndSend(RabbitMQConfig.CLASSIC_QUEUE, json);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUORUM_QUEUE, json);
            rabbitTemplate.convertAndSend(RabbitMQConfig.STREAM_QUEUE, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}