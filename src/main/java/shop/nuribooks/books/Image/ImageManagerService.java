package shop.nuribooks.books.Image;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.config.keymanager.KeyManagerConfig;

@Service
@RequiredArgsConstructor
public class ImageManagerService {
	private final KeyManagerConfig keyManagerConfig;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${cloud.nhn.image-manager.url}")
	private String imageManagerUrl;

	@Value("${cloud.nhn.image-manager.app-key}")
	private String imageAppKey;

	@Value("${cloud.nhn.image-manager.secret-id}")
	private String imageSecretId;

	public String uploadImage(MultipartFile file) throws IOException {
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		String path = "/nuribooks/" + fileName;
		String uploadUrl = imageManagerUrl + "/" + imageAppKey + "/images?path=" + path + "&overwirte=true";

		String secretKey = keyManagerConfig.getSecretValue(imageSecretId);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", secretKey);
		headers.set("Content-Type", "application/octet-stream");

		//파일 데이터 설정 및 헤더 구성
		HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

		//이미지 업로드 요청
		ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, String.class);

		if(response.getStatusCode().is2xxSuccessful()) {
			//JSON 응답 파싱하여 URL 추출
			JsonNode root = objectMapper.readTree(response.getBody());
			return root.path("file").path("url").asText();
		} else {
			throw new IOException("Image Manager 업로드 실패");
		}
	}
}
