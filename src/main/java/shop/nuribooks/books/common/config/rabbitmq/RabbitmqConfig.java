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

	private final KeyManagerConfig keyManagerConfig;

	@Value("${cloud.nhn.rabbitmq.secret-id}")
	String rabbitmqSecretId;

	public static final String BOOK_COUPON_QUEUE = "book.coupon.queue";
	public static final String CATEGORY_COUPON_QUEUE = "category.coupon.queue";

	public static final String COUPON_EXCHANGE = "coupon.exchange";

	public static final String BOOK_COUPON_ROUTING_KEY = "book.coupon";
	public static final String CATEGORY_COUPON_ROUTING_KEY = "category.coupon";

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
		return rabbitTemplate;
	}

	@Bean
	public Queue bookCouponQueue() {
		return new Queue(BOOK_COUPON_QUEUE, true);
	}

	@Bean
	public Queue categoryCouponQueue() {
		return new Queue(CATEGORY_COUPON_QUEUE, true);
	}

	@Bean
	public DirectExchange couponExchange() {
		return new DirectExchange(COUPON_EXCHANGE);
	}

	@Bean
	public Binding bookCouponBinding(Queue bookCouponQueue, DirectExchange couponExchange) {
		return BindingBuilder.bind(bookCouponQueue).to(couponExchange).with(BOOK_COUPON_ROUTING_KEY);
	}

	@Bean
	public Binding categoryCouponBinding(Queue categoryCouponQueue, DirectExchange couponExchange) {
		return BindingBuilder.bind(categoryCouponQueue).to(couponExchange).with(CATEGORY_COUPON_ROUTING_KEY);
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