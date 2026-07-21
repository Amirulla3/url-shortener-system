package analytics_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder
                .name("link-clicks-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
