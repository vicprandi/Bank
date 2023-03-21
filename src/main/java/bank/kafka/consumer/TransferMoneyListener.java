package bank.kafka.consumer;

import bank.kafka.model.EventDTO;
import bank.transaction.service.TransactionServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferMoneyListener implements TransferMoneyListenerInterface {
    @Autowired
    private TransactionServiceImpl transactionService;

    @KafkaListener(topics = "transactions", groupId = "group_id")
    public void consumeTransferMessage(ConsumerRecord<String, EventDTO> record) {
        try {
            EventDTO event = record.value();
            BigDecimal amount = new BigDecimal(String.valueOf(event.getAmount()));
            Long originAccount = Long.valueOf(event.getOriginAccount());
            Long destinationAccount = Long.valueOf(event.getRecipientAccount());
            transactionService.transferMoney(amount, originAccount, destinationAccount, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMoneyTransfer() {
        System.out.println("Money transfer completed!");
    }
}