package shop.nuribooks.books.common.Image;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
		if (originalFileName == null || originalFileName.isEmpty()) {
			originalFileName = "default_image.png";
		}

		String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);

		String path = "/nuribooks/" + encodedFileName;
		String uploadUrl = imageManagerUrl + "/" + imageAppKey + "/images?path=" + path + "&overwrite=true";

		HttpHeaders headers = createHeader();

		headers.set("Content-Type", "application/octet-stream");

		//파일 데이터 설정 및 헤더 구성
		HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

		//이미지 업로드 요청
		ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, String.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			//JSON 응답 파싱하여 URL 추출
			log.info("{}", response.getBody());
			JsonNode root = objectMapper.readTree(response.getBody());
			String imageUrl = root.path("file").path("url").asText();
			log.info("Image uploaded successfully. URL: {}", imageUrl);
			return root.path("file").path("url").asText();
		} else {
			log.error("Failed to upload image to NHN Image Manager. Status: {}, Response: {}", response.getStatusCode(),
				response.getBody());
			throw new IOException("Image Manager 업로드 실패");
		}
	}

	public List<String> uploadImages(List<MultipartFile> files) throws IOException {
		String basePath = "/nuribooks/review";
		String uploadUrl = imageManagerUrl + "/" + imageAppKey + "/images";

		HttpHeaders headers = createHeader();
		headers.set("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);

		//파일 데이터 설정 및 헤더 구성
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("params",
			"{\"basepath\": \"" + basePath + "\", \"overwrite\": true}");

		// 파일 첨부
		for (MultipartFile file : files) {
			String originalFilename = file.getOriginalFilename();
			String extension = originalFilename != null && originalFilename.contains(".")
				? originalFilename.substring(originalFilename.lastIndexOf("."))
				: "";

			// 고유한 이름 생성
			String uniqueFilename = UUID.randomUUID() + extension;

			// MultipartFile을 새 이름으로 저장
			File tempFile = File.createTempFile("upload-", uniqueFilename);
			file.transferTo(tempFile);

			// 파일을 body에 추가
			body.add("files", new FileSystemResource(tempFile));
		}

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity,
			String.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			//JSON 응답 파싱하여 URL 추출
			log.info("{}", response.getBody());
			JsonNode root = objectMapper.readTree(response.getBody());
			List<String> urls = new LinkedList<>();
			for (JsonNode node : root.path("successes")) {
				urls.add(node.path("url").asText());
			}
			log.info("Image uploaded successfully. URLs: {}", urls);
			return urls;
		} else {
			log.error("Failed to upload image to NHN Image Manager. Status: {}, Response: {}", response.getStatusCode(),
				response.getBody());
			throw new IOException("Image Manager 업로드 실패");
		}
	}

	private HttpHeaders createHeader() {
		String secretKey = keyManagerConfig.getSecretValue(imageSecretId);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", secretKey);
		return headers;
	}
}
