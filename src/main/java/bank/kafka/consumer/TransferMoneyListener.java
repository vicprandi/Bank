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
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private List<Transaction> consumeTransactionFromKafka() {
        List<Transaction> transactions = new ArrayList<>();

        //Criando o consumidor com o createConsumer.
        Consumer<String, EventDTO> consumer = consumerFactory.createConsumer();
        //O método subscribe  inscreve o consumidor no tópico "transactions" para começar a receber mensagens Kafka desse tópico.
        consumer.subscribe(Collections.singleton("transactions"));

        ExecutorService executor = Executors.newFixedThreadPool(10);

        while (true) {
            //Poll é responsável por realizar a leitura de registros do Kafka.
            //Deve esperar no máximo 100 milissegundos para obter registros antes de retornar vazio.
            ConsumerRecords<String, EventDTO> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, EventDTO> record : records) {
                String message = String.valueOf(record.value());
                logger.info("Received message from Kafka: {}", message);

                EventDTO event = record.value();
                executor.submit(() -> (List<Transaction>) transactionService.processEvent(event));
                // Salva a transação da conta de origem
                transactions.add(transactionService.createTransaction(event.getAmount(), accountRepository.findByAccountNumber(Long.valueOf(event.getOriginAccount())), Transaction.TransactionEnum.TRANSFER));
            }
            //Assíncrono!
            consumer.commitAsync();
        }
    }

    @Override
    public void onMoneyTransfer() {
        logger.info("Money transfer completed.");
    }
}