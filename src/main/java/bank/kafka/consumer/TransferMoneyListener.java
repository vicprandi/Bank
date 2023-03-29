package bank.kafka.consumer;

import bank.account.repository.AccountRepository;
import bank.kafka.model.EventDTO;
import bank.kafka.producer.KafkaService;
import bank.model.Transaction;
import bank.transaction.service.TransactionServiceImpl;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransferMoneyListener implements TransferMoneyListenerInterface {
    private final AccountRepository accountRepository;
    private final ConsumerFactory<String, EventDTO> consumerFactory;
    private final TransactionServiceImpl transactionService;
    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public TransferMoneyListener(AccountRepository accountRepository, ConsumerFactory<String, EventDTO> consumerFactory, TransactionServiceImpl transactionService) {
        this.accountRepository = accountRepository;
        this.consumerFactory = consumerFactory;
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "transactions", groupId = "group_id", containerFactory = "concurrentKafkaListenerContainerFactory")
    private List<Transaction> consumeTransactionFromKafka() {
        List<Transaction> transactions = new ArrayList<>();

        Consumer<String, EventDTO> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton("transactions"));

        while (true) {
            ConsumerRecords<String, EventDTO> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, EventDTO> record : records) {
                String message = String.valueOf(record.value());
                logger.info("Received message from Kafka: {}", message);

                EventDTO event = record.value();
                transactionService.processEvent(event);
                // Salva a transação da conta de origem
                transactions.add(transactionService.createTransaction(event.getAmount(), accountRepository.findByAccountNumber(Long.valueOf(event.getOriginAccount())), Transaction.TransactionEnum.TRANSFER));
            }
            consumer.commitSync();
        }
    }

    @Override
    public void onMoneyTransfer() {
        logger.info("Money transfer completed.");
    }
}