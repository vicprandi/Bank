package bank.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    public KafkaConfiguration(@Value("${bootstrap-servers}") String bootstrapServers) {
        createTopic(bootstrapServers);
    }

    /***
    Essa classe será executada ao iniciar o aplicativo Spring e criará o tópico chamado "transactions" com 1 partição e um fator de replicação de 1.
    Essa operação deve ser realizada apenas uma vez antes de começar a produzir ou consumir mensagens no Kafka.
     ***/

    private void createTopic(String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        AdminClient adminClient = AdminClient.create(props);

        NewTopic topic = new NewTopic("transactions", 1, (short) 1);
        adminClient.createTopics(Collections.singleton(topic));

        adminClient.close();
    }
}