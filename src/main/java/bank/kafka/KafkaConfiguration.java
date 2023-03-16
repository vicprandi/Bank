package bank.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;
import java.util.Properties;

/***
 Essa classe será executada ao iniciar o aplicativo Spring e criará o tópico chamado "transactions" com 1 partição e um fator de replicação de 1.
 Essa operação deve ser realizada apenas uma vez antes de começar a produzir ou consumir mensagens no Kafka.
 Eu tinha colocado o Configuration no lugar errado!
 ***/

@Configuration
@PropertySource("classpath:application.properties")
public class KafkaConfiguration {

    public KafkaConfiguration(@Value("${bootstrap-servers}") String bootstrapServers,
                              @Value("${topic.name}") String topicName) {
        createTopic(bootstrapServers, topicName);
    }

    private void createTopic(String bootstrapServers, String topicName) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        AdminClient adminClient = AdminClient.create(props);

        NewTopic topic = new NewTopic(topicName, 1, (short) 1);
        adminClient.createTopics(Collections.singleton(topic));

        adminClient.close();
    }
}
