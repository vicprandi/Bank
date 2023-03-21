package bank.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

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

    @Value("${bootstrap-servers}")
    private String bootstrapServers;

    @Value("${topic.name}")
    private String topicName;

    public KafkaConfiguration(@Value("${bootstrap-servers}") String bootstrapServers,
                              @Value("${topic.name}") String topicName) {
        createTopic(bootstrapServers, topicName);
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
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
