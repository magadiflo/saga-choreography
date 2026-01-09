package dev.magadiflo.commons.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMessagingConstants {
    public static final String TOPIC_ORDER_CREATED = "order.created";

    public static final String TOPIC_PAYMENT_PROCESSED = "payment.processed";
    public static final String TOPIC_PAYMENT_FAILED = "payment.failed";
    public static final String TOPIC_PAYMENT_REFUNDED = "payment.refunded";

    public static final String TOPIC_INVENTORY_FAILED = "inventory.failed";
}
