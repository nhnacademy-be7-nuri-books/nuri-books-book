package shop.nuribooks.books.common.image;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ImageController.class)
class ImageManagerControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	ImageManagerService imageManagerService;

	@Test
	void uploadImageTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("filename",
			"contentimnida".getBytes(StandardCharsets.UTF_8));
		String result = "resultUrl";
		when(imageManagerService.uploadImage(any())).thenReturn(result);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/uploadImage")
				.file("file", multipartFile.getBytes()))
			.andExpect(status().isOk())
			.andExpect(content().string(result));
	}

	@Test
	void uploadImageTestFail() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("filename",
			"contentimnida".getBytes(StandardCharsets.UTF_8));
		String result = "ImageUpload Fail";
		when(imageManagerService.uploadImage(any())).thenThrow(IOException.class);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/uploadImage")
				.file("file", multipartFile.getBytes()))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string(result));
	}

	@Test
	void uploadImagesTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("filename",
			"contentimnida".getBytes(StandardCharsets.UTF_8));
		String result = "resultUrl";
		List<String> resList = List.of(result);
		when(imageManagerService.uploadImages(anyList(), anyString())).thenReturn(resList);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image/bulk")
				.file("files", multipartFile.getBytes())
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").value(result));
	}

	@Test
	void uploadImagesTestFail() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("filename",
			"contentimnida".getBytes(StandardCharsets.UTF_8));
		when(imageManagerService.uploadImages(anyList(), anyString())).thenThrow(IOException.class);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image/bulk")
				.file("files", multipartFile.getBytes())
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
			.andExpect(status().is5xxServerError());
	}
}
