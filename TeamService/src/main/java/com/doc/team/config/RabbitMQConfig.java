package com.doc.team.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String TEAM_CREATE_QUEUE = "com.team.create";
    public static final String TEAM_ADD_MEMBER_QUEUE = "com.team.addMember";
    public static final String TEAM_DELETE_MEMBER_QUEUE = "com.team.deleteMember";

    /**
     * 创建RabbitTemplate bean，用于发送消息
     *
     * @param connectionFactory 连接工厂
     * @return 配置好的RabbitTemplate实例
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 配置消息转换器，用于将Java对象转换为JSON格式的消息
     *
     * @return 配置好的MessageConverter实例
     */
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建并配置一个Direct类型的交换机
     *
     * @return 配置好的DirectExchange实例
     */
    @Bean
    public DirectExchange teamExchange() {
        return new DirectExchange("com.team");
    }
}
