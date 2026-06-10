package co.empresa.proyecto_desarrollo3.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Exchange ─────────────────────────────────────────────────────────────
    public static final String ORDERS_EXCHANGE    = "orders.exchange";
    public static final String PAYMENTS_EXCHANGE  = "payments.exchange";

    // ── Queues ────────────────────────────────────────────────────────────────
    public static final String ANALYTICS_ORDER_QUEUE   = "analytics.order.queue";
    public static final String ANALYTICS_PAYMENT_QUEUE = "analytics.payment.queue";

    // ── Routing keys ─────────────────────────────────────────────────────────
    public static final String ORDER_CONFIRMED_KEY  = "order.confirmed";
    public static final String PAYMENT_RESULT_KEY   = "payment.result";

    // ── Exchanges ─────────────────────────────────────────────────────────────
    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(ORDERS_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange(PAYMENTS_EXCHANGE, true, false);
    }

    // ── Queues ────────────────────────────────────────────────────────────────
    @Bean
    public Queue analyticsOrderQueue() {
        return QueueBuilder.durable(ANALYTICS_ORDER_QUEUE).build();
    }

    @Bean
    public Queue analyticsPaymentQueue() {
        return QueueBuilder.durable(ANALYTICS_PAYMENT_QUEUE).build();
    }

    // ── Bindings ──────────────────────────────────────────────────────────────
    @Bean
    public Binding orderQueueBinding(Queue analyticsOrderQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(analyticsOrderQueue)
                .to(ordersExchange)
                .with(ORDER_CONFIRMED_KEY);
    }

    @Bean
    public Binding paymentQueueBinding(Queue analyticsPaymentQueue, TopicExchange paymentsExchange) {
        return BindingBuilder.bind(analyticsPaymentQueue)
                .to(paymentsExchange)
                .with(PAYMENT_RESULT_KEY);
    }

    // ── Serialization ─────────────────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
