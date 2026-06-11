package co.empresa.proyecto_desarrollo3.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // ── Exchange ─────────────────────────────────────────────────────────────
    public static final String ORDERS_EXCHANGE = "order.exchange";
    public static final String PAYMENTS_EXCHANGE  = "payments.exchange";

    // ── Queues ────────────────────────────────────────────────────────────────
    public static final String ORDER_CREATED_QUEUE  = "order.created.queue";
    public static final String PAYMENT_RESULT_QUEUE = "payment.result.queue";

    // ── Routing keys ─────────────────────────────────────────────────────────
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String PAYMENT_RESULT_KEY   = "payment.result";

    // ── Exchanges ─────────────────────────────────────────────────────────────
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(ORDERS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange paymentsExchange() {
        return new DirectExchange(PAYMENTS_EXCHANGE, true, false);
    }

    // ── Queues ────────────────────────────────────────────────────────────────
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue paymentResultQueue() {
        return QueueBuilder.durable(PAYMENT_RESULT_QUEUE).build();
    }

    // ── Bindings ──────────────────────────────────────────────────────────────
    @Bean
    public Binding orderQueueBinding(Queue orderCreatedQueue, DirectExchange ordersExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(ordersExchange)
                .with(ORDER_CREATED_KEY);
    }

    @Bean
    public Binding paymentQueueBinding(Queue paymentResultQueue, DirectExchange paymentsExchange) {
        return BindingBuilder.bind(paymentResultQueue)
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
