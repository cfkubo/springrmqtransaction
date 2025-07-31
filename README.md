# 🐇 SpringRMQ Transaction Simulator 

Welcome to **SpringRMQ Transaction Simulator**!  
This project is a fun, interactive demo of how to use RabbitMQ exchanges, queues, publisher confirms, and Vaadin dashboards in a modern Spring Boot app.

## 🚀 What does it do?

- **Simulates banking transactions** and publishes them to a RabbitMQ **fanout exchange** called `transactions`.
- The exchange routes every transaction to **three different queues**: classic, quorum, and stream.
- **Publisher confirms** are tracked for reliability.
- **Consumers** read messages from all three queues, 10 at a time, every second, and acknowledge them.
- A **Vaadin UI dashboard** shows live counters for published and confirmed messages per queue.

## 🛠️ How to build & run

### Prerequisites

- Java 21
- Maven
- RabbitMQ running locally (`localhost:5672`)
  - Username: `arul`
  - Password: `password`
  - [Install RabbitMQ](https://www.rabbitmq.com/download.html) if you don't have it.

### Quick Start

1. **Clone this repo**
   ```sh
   git clone https://github.com/yourusername/springrmqtransaction.git
   cd springrmqtransaction
   ```


```
rabbitmqctl add_user arul password
rabbitmqctl set_permissions -p / arul ".*" ".*" ".*"
```

```
mvn clean package
```

```
mvn spring-boot:run
```