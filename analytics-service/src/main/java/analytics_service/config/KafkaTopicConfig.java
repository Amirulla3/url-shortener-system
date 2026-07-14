package analytics_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic linkClicksTpoic(){
        return new NewTopic(
                "link-clicks",
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic deadLettertopic(){
        return new NewTopic(
                "link-clicks-dlt",
                1,
                (short) 1
        );
    }
}
