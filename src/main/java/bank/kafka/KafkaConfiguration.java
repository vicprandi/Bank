package bank.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableKafka
@PropertySource("classpath:application.properties")
public class KafkaConfiguration {

    /***
     * Comandos a serem utilizados:
     * docker exec -it bank_kafka_1 bash
     * docker exec bank kafka-topics --describe --topic transactions --bootstrap-server localhost:9092
     ***/

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${topic.name}")
    private String topicName;


    //Este é um construtor de uma classe Java que cria um tópico no Kafka utilizando
    // as informações passadas por parâmetro, como o endereço do servidor Kafka e o nome do tópico.
    public KafkaConfiguration(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                              @Value("${topic.name}") String topicName) {
        createTopic(bootstrapServers, topicName);
    }

    //Este é um método de configuração do KafkaAdmin que cria um bean de administração do Kafka.
    // Ele define as configurações do KafkaAdmin a partir do endereço do servidor Kafka,
    // utilizando uma estrutura de mapas.
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }


   //Este é um método que cria um tópico no Kafka com um nome especificado e configurações padrão,
   //como a quantidade de partições e a replicação dos dados.
   //Ele utiliza o KafkaAdmin para criar o tópico e fecha a conexão ao final.
    private void createTopic(String bootstrapServers, String topicName) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        AdminClient adminClient = AdminClient.create(props);

        NewTopic topic = new NewTopic(topicName, 1, (short) 1);
        adminClient.createTopics(Collections.singleton(topic));

        adminClient.close();
    }
}
