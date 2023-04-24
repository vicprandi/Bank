package bank.kafka.model;

import bank.kafka.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Event event;
    private BigDecimal amount;
    private Long transactionId;
}
