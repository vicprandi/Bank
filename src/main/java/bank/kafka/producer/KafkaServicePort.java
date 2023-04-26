package bank.kafka.producer;

import bank.kafka.model.Event;

import java.math.BigDecimal;

public interface KafkaServicePort {
    void sendKafka(
            Event event,
            BigDecimal amount,
            Long transactionId
    );
}
