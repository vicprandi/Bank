package bank.kafka.consumer;

import bank.kafka.consumer.TransferMoneyListener;
import bank.kafka.model.Event;
import bank.kafka.model.EventDTO;
import bank.transaction.service.TransactionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferMoneyListenerTest {

    @InjectMocks
    private TransferMoneyListener transferMoneyListener;

    @Mock
    private TransactionService transactionService;

    @Test
    public void shouldExecuteTransfer_whenConsumeTransferMessageIsCalled() {
        // Arrange
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEvent(Event.SAVE_TRANSFER);
        eventDTO.setTransactionId(12345L);
        ConsumerRecord<String, EventDTO> record = new ConsumerRecord<>("transactions", 0, 0, "key", eventDTO);

        // Act
        transferMoneyListener.consumeTransferMessage(record);

        // Assert
        verify(transactionService, times(1)).executeTransfer(12345L);
    }

    @Test
    public void shouldNotExecuteTransfer_whenEventIsNotSaveTransfer() {
        // Arrange
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEvent(Event.FAILED); // Set an event other than SAVE_TRANSFER
        eventDTO.setTransactionId(12345L);
        ConsumerRecord<String, EventDTO> record = new ConsumerRecord<>("transactions", 0, 0, "key", eventDTO);

        // Act
        transferMoneyListener.consumeTransferMessage(record);

        // Assert
        verify(transactionService, never()).executeTransfer(anyLong());
    }
}
