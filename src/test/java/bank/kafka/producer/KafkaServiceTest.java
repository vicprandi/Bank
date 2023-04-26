package bank.kafka.producer;

import bank.kafka.model.Event;
import bank.kafka.model.EventDTO;
import bank.kafka.producer.KafkaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaServiceTest {

    @InjectMocks
    private KafkaService kafkaService;

    @Mock
    private KafkaTemplate<String, EventDTO> kafkaTemplate;

    @Test
    public void shouldSendMessageToKafka_whenSendKafkaIsCalled() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100.0);
        Long transactionId = 12345L;

        // Act
        kafkaService.sendKafka(Event.SAVE_TRANSFER, amount, transactionId);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("transactions"), any(EventDTO.class));
    }
}
