package bank.kafka.producer;

import bank.kafka.TransferStatus;
import bank.kafka.model.Event;
import bank.kafka.model.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class KafkaService implements KafkaServicePort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    public KafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendKafka(
            Event event,
            BigDecimal amount,
            String originAccount,
            String recipientAccount) {
        EventDTO eventDTO = new EventDTO(
                event,
                amount,
                originAccount,
                recipientAccount,
                TransferStatus.PENDING
        );

        kafkaTemplate.send("transactions", eventDTO);
        log.info("message successfully sent to the broker {}", eventDTO.getEvent());
    }
}