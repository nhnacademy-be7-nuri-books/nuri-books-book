package shop.nuribooks.books.common.image;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.config.keymanager.KeyManagerConfig;

@ExtendWith(MockitoExtension.class)
class ImageManagerServiceTest {
	@InjectMocks
	ImageManagerService imageManagerService;
	@Mock
	KeyManagerConfig keyManagerConfig;
	@Mock
	RestTemplate restTemplate;
	@Mock
	ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		when(keyManagerConfig.getSecretValue(any())).thenReturn("secretKey");
	}

	@Test
	void uploadSingleImageWithOutOriginalFileNameNull400() {
		MultipartFile mf = new MockMultipartFile("fileName", null, "application/json",
			"contents".getBytes(StandardCharsets.UTF_8));
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(400)).body("failed..");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		Assertions.assertThrows(IOException.class, () -> imageManagerService.uploadImage(mf));
	}

	@Test
	void uploadSingleImageWithOutOriginalFileNameEmpty400() {
		MultipartFile mf = getNulltipartFile();
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(400)).body("failed..");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		Assertions.assertThrows(IOException.class, () -> imageManagerService.uploadImage(mf));
	}

	@Test
	void uploadSingleImageSuccess() throws IOException {
		MultipartFile mf = new MockMultipartFile("fileName", "file.png", "application/json",
			"contents".getBytes(StandardCharsets.UTF_8));
		String expectedUrl = "https://example.com/uploaded-image.png";
		String responseJson = "{\"file\": {\"url\": \"" + expectedUrl + "\"}}";
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(200)).body(responseJson);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		when(objectMapper.readTree(responseJson)).thenReturn(new ObjectMapper().readTree(responseJson));
		Assertions.assertEquals(expectedUrl, imageManagerService.uploadImage(mf));
	}

	@Test
	void uploadMultiImageSuccess() throws IOException {
		MultipartFile mf = new MockMultipartFile("fileName", "file.png", "application/json",
			"contents".getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> list = List.of(mf);
		String expectedUrl = "https://example.com/uploaded-image.png";
		String responseJson = "{\"successes\": [{\"url\": \"" + expectedUrl + "\"}]}";
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(200)).body(responseJson);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		when(objectMapper.readTree(responseJson)).thenReturn(new ObjectMapper().readTree(responseJson));
		Assertions.assertIterableEquals(List.of(expectedUrl),
			imageManagerService.uploadImages(list, "/nuribooks"));
	}

	@Test
	void uploadNullMultiImageSuccess() throws IOException {
		MultipartFile mf = getNulltipartFile();
		List<MultipartFile> list = List.of(mf);
		String expectedUrl = "https://example.com/uploaded-image.png";
		String responseJson = "{\"successes\": [{\"url\": \"" + expectedUrl + "\"}]}";
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(200)).body(responseJson);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		when(objectMapper.readTree(responseJson)).thenReturn(new ObjectMapper().readTree(responseJson));
		Assertions.assertIterableEquals(List.of(expectedUrl),
			imageManagerService.uploadImages(list, "/nuribooks"));
	}

	@Test
	void uploadNoDotMultiImageSuccess() throws IOException {
		MultipartFile mf = new MockMultipartFile("fileName", "filepng", "application/json",
			"contents".getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> list = List.of(mf);
		String expectedUrl = "https://example.com/uploaded-image.png";
		String responseJson = "{\"successes\": [{\"url\": \"" + expectedUrl + "\"}]}";
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(200)).body(responseJson);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		when(objectMapper.readTree(responseJson)).thenReturn(new ObjectMapper().readTree(responseJson));
		Assertions.assertIterableEquals(List.of(expectedUrl),
			imageManagerService.uploadImages(list, "/nuribooks"));
	}

	@Test
	void uploadMultiImageFail() {
		MultipartFile mf = new MockMultipartFile("fileName", "file.png", "application/json",
			"contents".getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> list = List.of(mf);
		String expectedUrl = "https://example.com/uploaded-image.png";
		String responseJson = "{\"successes\": [{\"url\": \"" + expectedUrl + "\"}]}";
		ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(400)).body(responseJson);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
			.thenReturn(response);
		Assertions.assertThrows(IOException.class, () -> imageManagerService.uploadImages(list, "/nuribooks"));
	}

	private MultipartFile getNulltipartFile() {
		return new MultipartFile() {
			@Override
			public String getName() {
				return "name";
			}

			@Override
			public String getOriginalFilename() {
				return null;
			}

			@Override
			public String getContentType() {
				return "";
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public long getSize() {
				return 4;
			}

			@Override
			public byte[] getBytes() throws IOException {
				return "good".getBytes(StandardCharsets.UTF_8);
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return null;
			}

			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {

			}
		};
	}
}
