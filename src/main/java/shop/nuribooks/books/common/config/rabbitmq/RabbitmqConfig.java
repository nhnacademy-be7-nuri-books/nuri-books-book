package shop.nuribooks.books.common.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
		return rabbitTemplate;
	}

	@Bean
	public Queue inventoryUpdateQueue() {
		return new Queue(INVENTORY_UPDATE_KEY, true);
	}

	@Bean
	public DirectExchange inventoryExchange() {
		return new DirectExchange(INVENTORY_EXCHANGE);
	}

	@Bean
	public Binding inventoryUpdate(Queue inventoryUpdateQueue, DirectExchange inventoryExchange) {
		return BindingBuilder.bind(inventoryUpdateQueue).to(inventoryExchange).with(INVENTORY_ROUTING_KEY);
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
