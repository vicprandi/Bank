package bank.kafka.consumer;

import bank.kafka.model.EventDTO;
import bank.transaction.service.TransactionServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferMoneyListener implements TransferMoneyListenerInterface {
    @Autowired
    public TransactionServiceImpl transactionService;

    @KafkaListener(topics = "transactions", groupId = "group_id", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void consumeTransferMessage(ConsumerRecord<String, EventDTO> record) {
        try {

            EventDTO event = record.value();
            BigDecimal amount = new BigDecimal(String.valueOf(event.getAmount()));
            Long originAccount = Long.valueOf(event.getOriginAccount());
            Long destinationAccount = Long.valueOf(event.getRecipientAccount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMoneyTransfer() {
        System.out.println("Money transfer completed!");
    }
}