package rmq.example.springrmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CLASSIC_QUEUE = "classic.transactions";
    public static final String QUORUM_QUEUE = "quorum.transactions";
    public static final String STREAM_QUEUE = "stream.transactions";

    @Bean
    public Queue classicQueue() {
        return QueueBuilder.durable(CLASSIC_QUEUE).build();
    }

    @Bean
    public Queue quorumQueue() {
        return QueueBuilder.durable(QUORUM_QUEUE)
                .quorum()
                .build();
    }

    @Bean
    public Queue streamQueue() {
        return QueueBuilder.durable(STREAM_QUEUE)
                .stream()
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password
    ) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        return template;
    }
}