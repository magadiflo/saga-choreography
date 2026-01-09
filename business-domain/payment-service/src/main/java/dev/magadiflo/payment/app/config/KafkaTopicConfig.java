package dev.magadiflo.payment.app.config;

import dev.magadiflo.commons.constants.OrderMessagingConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name(OrderMessagingConstants.TOPIC_PAYMENT_PROCESSED).build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(OrderMessagingConstants.TOPIC_PAYMENT_FAILED).build();
    }

    @Bean
    public NewTopic paymentRefundedTopic() {
        return TopicBuilder.name(OrderMessagingConstants.TOPIC_PAYMENT_REFUNDED).build();
    }
}
