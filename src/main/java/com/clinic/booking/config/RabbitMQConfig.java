package com.clinic.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "email_queue";
    public static final String EXCHANGE_NAME = "email_exchange";
    public static final String ROUTING_KEY = "email_routing_key";

    // Khởi tạo Queue
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // true = Queue sẽ tồn tại ngay cả khi RabbitMQ bị khởi động lại (durable)
    }

    // Khởi tạo Exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Nối Queue vào Exchange thông qua Routing Key
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // Cấu hình chuyển đổi Object thành chuỗi JSON trước khi đẩy vào Queue
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
