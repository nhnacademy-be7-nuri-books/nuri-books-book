package shop.nuribooks.books.common.config.keymanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import shop.nuribooks.books.common.config.keymanager.dto.KeyManagerResponse;

@Component
public class KeyManagerConfig {
	@Value("${cloud.nhn.key-manager.url}")
	private String keyManagerUrl;

	@Value("${cloud.nhn.key-manager.app-key}")
	private String appKey;

	@Value("${cloud.nhn.key-manager.access-key-id}")
	private String accessKeyId;

	@Value("${cloud.nhn.key-manager.secret-access-key}")
	private String secretAccessKey;

	//Secure Key Manager에 저장한 기밀 데이터를 조회합니다.
	public String getSecretValue(String keyId) {
		RestTemplate restTemplate = new RestTemplate();

		String url = String.format("%s/keymanager/v1.2/appkey/%s/secrets/%s", keyManagerUrl, appKey, keyId);

		//키매니저 필수 헤더
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-TC-AUTHENTICATION-ID", accessKeyId);
		headers.set("X-TC-AUTHENTICATION-SECRET", secretAccessKey);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<KeyManagerResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity,
			KeyManagerResponse.class);

		return response.getBody() != null && response.getBody().body() != null ? response.getBody().body().secret() :
			null;
	}
}