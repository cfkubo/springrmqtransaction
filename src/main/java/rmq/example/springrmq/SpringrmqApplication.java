package rmq.example.springrmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SpringrmqApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringrmqApplication.class, args);
    }
}
