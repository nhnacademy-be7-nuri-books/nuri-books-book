package shop.nuribooks.books.common.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.config.keymanager.KeyManagerConfig;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitmqConfig {

	public static final String INVENTORY_UPDATE_KEY = "nuribooks.inventory.update.queue";
	public static final String INVENTORY_EXCHANGE = "nuribooks.inventory.exchange";
	public static final String INVENTORY_ROUTING_KEY = "nuribooks.inventory.update";
	public static final String INVENTORY_DLQ = "nuribooks.inventory.update.dlq";
	public static final String DEAD_LETTER_EXCHANGE = "nuribooks.deadletter.exchange";
	public static final String INVENTORY_DEAD_LETTER_ROUTING_KEY = "nuribooks.inventory.update.dlq";
	private final KeyManagerConfig keyManagerConfig;
	@Value("${cloud.nhn.rabbitmq.secret-id}")
	String rabbitmqSecretId;

	@Bean
	public ConnectionFactory connectionFactory() {
		String rabbitmqConfig = keyManagerConfig.getSecretValue(rabbitmqSecretId);

		RabbitmqConfigResponse response = parseRabbitmqConfig(rabbitmqConfig);

		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(response.host());
		connectionFactory.setPort(response.port());
		connectionFactory.setUsername(response.userName());
		connectionFactory.setPassword(response.password());
		connectionFactory.setVirtualHost(response.vhost());
		return connectionFactory;
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		rabbitTemplate.setReplyTimeout(20000L);
		rabbitTemplate.setChannelTransacted(true);

		// 모든 메시지 Persistent 설정
		rabbitTemplate.setBeforePublishPostProcessors(message -> {
			message.getMessageProperties().setContentType("application/json");
			message.getMessageProperties().setContentEncoding("UTF-8");
			message.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
			return message;
		});

		return rabbitTemplate;
	}

	//Dead Letter Queue 설정 포함한 큐 정의
	@Bean
	public Queue inventoryUpdateQueue() {
		return QueueBuilder.durable(INVENTORY_UPDATE_KEY)
			.withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
			.withArgument("x-dead-letter-routing-key", INVENTORY_DEAD_LETTER_ROUTING_KEY)
			.build();
	}

	//DLQ용 큐 생성
	@Bean
	public Queue inventoryUpdateDLQ() {
		return new Queue(INVENTORY_DLQ, true);
	}

	@Bean
	public DirectExchange inventoryExchange() {
		return new DirectExchange(INVENTORY_EXCHANGE);
	}

	@Bean
	public DirectExchange deadLetterExchange() {
		return new DirectExchange(DEAD_LETTER_EXCHANGE);
	}

	@Bean
	public Binding inventoryUpdateBinding() {
		return BindingBuilder.bind(inventoryUpdateQueue())
			.to(inventoryExchange())
			.with(INVENTORY_ROUTING_KEY);
	}

	@Bean
	public Binding inventoryDLQBinding() {
		return BindingBuilder.bind(inventoryUpdateDLQ())
			.to(deadLetterExchange())
			.with(INVENTORY_DEAD_LETTER_ROUTING_KEY);
	}

	private RabbitmqConfigResponse parseRabbitmqConfig(String rabbitmqConfig) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(rabbitmqConfig, RabbitmqConfigResponse.class);
		} catch (Exception e) {
			throw new RuntimeException("RabbitMQ 키매니저 파싱 실패", e);
		}
	}
}
