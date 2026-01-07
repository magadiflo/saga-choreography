package dev.magadiflo.order.app.config;

import dev.magadiflo.order.app.constants.OrderMessagingConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(OrderMessagingConstants.TOPIC_ORDER_CREATED).build();
    }
}
