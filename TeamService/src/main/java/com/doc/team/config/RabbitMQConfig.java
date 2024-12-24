package com.doc.team.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    /**
     * 创建团队队列名称。
     */
    public static final String TEAM_CREATE_QUEUE = "com.team.create";

    /**
     * 添加成员到团队队列名称。
     */
    public static final String TEAM_ADD_MEMBER_QUEUE = "com.team.addMember";

    /**
     * 从团队中删除成员队列名称。
     */
    public static final String TEAM_DELETE_MEMBER_QUEUE = "com.team.deleteMember";

    /**
     * 团队操作响应队列名称。
     */
    public static final String TEAM_RESPONSE_QUEUE = "com.team.response";

    /**
     * 团队操作交换机名称。
     */
    public static final String TEAM_EXCHANGE = "com.team";

    /**
     * 响应消息路由键。
     */
    public static final String TEAM_RESPONSE_ROUTING_KEY = "com.team.response";

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

    @Bean
    public Queue teamCreateQueue() {
        return new Queue(TEAM_CREATE_QUEUE, false);
    }

    @Bean
    public Queue teamAddMemberQueue() {
        return new Queue(TEAM_ADD_MEMBER_QUEUE, false);
    }

    @Bean
    public Queue teamDeleteMemberQueue() {
        return new Queue(TEAM_DELETE_MEMBER_QUEUE, false);
    }

    @Bean
    public Queue teamResponseQueue() {
        return new Queue(TEAM_RESPONSE_QUEUE, false);
    }

    @Bean
    public DirectExchange teamExchange() {
        return new DirectExchange(TEAM_EXCHANGE);
    }

    @Bean
    public Binding bindCreateQueueToExchange(Queue teamCreateQueue, DirectExchange teamExchange) {
        return BindingBuilder.bind(teamCreateQueue).to(teamExchange).with(TEAM_CREATE_QUEUE);
    }

    @Bean
    public Binding bindAddMemberQueueToExchange(Queue teamAddMemberQueue, DirectExchange teamExchange) {
        return BindingBuilder.bind(teamAddMemberQueue).to(teamExchange).with(TEAM_ADD_MEMBER_QUEUE);
    }

    @Bean
    public Binding bindDeleteMemberQueueToExchange(Queue teamDeleteMemberQueue, DirectExchange teamExchange) {
        return BindingBuilder.bind(teamDeleteMemberQueue).to(teamExchange).with(TEAM_DELETE_MEMBER_QUEUE);
    }

    @Bean
    public Binding bindResponseQueueToExchange(Queue teamResponseQueue, DirectExchange teamExchange) {
        return BindingBuilder.bind(teamResponseQueue).to(teamExchange).with(TEAM_RESPONSE_ROUTING_KEY);
    }
}
