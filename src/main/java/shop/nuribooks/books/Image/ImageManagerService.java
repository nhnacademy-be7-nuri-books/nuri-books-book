package shop.nuribooks.books.Image;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.config.keymanager.KeyManagerConfig;

@Slf4j
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
		String originalFileName = file.getOriginalFilename();
		if(originalFileName == null || originalFileName.isEmpty()) {
			originalFileName = "default_image.png";
		}

		String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);

		String path = "/nuribooks/" + encodedFileName;
		String uploadUrl = imageManagerUrl + "/" + imageAppKey + "/images?path=" + path + "&overwrite=true";

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
			log.info("{}", response.getBody());
			JsonNode root = objectMapper.readTree(response.getBody());
			String imageUrl = root.path("file").path("url").asText();
			log.info("Image uploaded successfully. URL: {}", imageUrl);
			return root.path("file").path("url").asText();
		} else {
			log.error("Failed to upload image to NHN Image Manager. Status: {}, Response: {}", response.getStatusCode(), response.getBody());
			throw new IOException("Image Manager 업로드 실패");
		}
	}
}
