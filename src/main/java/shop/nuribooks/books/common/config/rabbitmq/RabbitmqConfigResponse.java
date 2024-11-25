package shop.nuribooks.books.common.config.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RabbitmqConfigResponse(
	String host,
	int port,
	@JsonProperty("username")
	String userName,
	String password
) {
}
