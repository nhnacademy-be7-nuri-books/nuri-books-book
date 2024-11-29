package shop.nuribooks.books.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.config.keymanager.KeyManagerConfig;

@Configuration
@RequiredArgsConstructor
public class ElasticSearchConfig extends ElasticsearchConfiguration {
	private final KeyManagerConfig keyManagerConfig;

	@Value("${spring.elasticsearch.username}")
	private String username;

	@Value("${spring.elasticsearch.password}")
	private String encodedPassword;

	@Value("${spring.elasticsearch.uris}")
	private String encodedUri;

	@Override
	public ClientConfiguration clientConfiguration() {
		String password = keyManagerConfig.getSecretValue(encodedPassword);
		String uri = keyManagerConfig.getSecretValue(encodedUri);

		return ClientConfiguration.builder()
			.connectedTo(uri)
			.withBasicAuth(username, password)
			.build();
	}
}