package bank.kafka.consumer;

import bank.kafka.model.Event;
import bank.kafka.model.EventDTO;
import bank.kafka.producer.KafkaService;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.service.TransactionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

@Service
public class TransferMoneyListener implements TransferMoneyListenerInterface {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    private final Logger log = LoggerFactory.getLogger(KafkaService.class);


    //Eu preciso que quando ele fizer uma solicitação no endpoint de transfer, ele vá no serviço de transfer, faça as validações e envie para o kafka a mensagem,
    // e quando o consumer escutar lá do tópico, valide as informações e entre no outro método chamado "executeTransfer" onde vai de fato fazer a transferência.
    // Isso vai retornar um ID, que eu vou consultar num outro endpoint que será GET (não post) e devolver a transação feita. //

    @KafkaListener(topics = "transactions", groupId = "group_id", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void consumeTransferMessage(ConsumerRecord<String, EventDTO> record) {
        try {
            log.info("Starting consumer");

            EventDTO event = record.value();

            if (event.getEvent() == Event.SAVE_TRANSFER) {
                long transactionId = event.getTransactionId();

                transactionService.executeTransfer(transactionId);
                log.info("Finishing consumer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onMoneyTransfer() {
//        synchronized (this) {
//            this.notifyAll();
//        }
}