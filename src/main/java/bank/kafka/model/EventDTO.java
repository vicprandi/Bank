package bank.kafka.model;

import bank.kafka.TransferStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EventDTO {
    private Event event;
    private BigDecimal amount;
    private String originAccount;
    private String recipientAccount;
    private TransferStatus status;


    public EventDTO(Event event, BigDecimal amount, String originAccount, String recipientAccount, TransferStatus status) {
        this.event = event;
        this.amount = amount;
        this.originAccount = originAccount;
        this.recipientAccount = recipientAccount;
        this.status = status;
    }
}
