package shop.nuribooks.books.common.image;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageController {
	private final ImageManagerService imageManagerService;
	private final String rootPath = "/nuribooks";

	@Operation(summary = "이미지 업로드", description = "이미지를 업로드하고 해당 이미지의 URL을 반환하는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
		@ApiResponse(responseCode = "500", description = "이미지 업로드 실패",
			content = @Content(mediaType = "application/json"))
	})
	@PostMapping("/api/books/uploadImage")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
		try {
			String imageUrl = imageManagerService.uploadImage(file);
			return ResponseEntity.ok(imageUrl);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ImageUpload Fail");
		}
	}

	@Operation(summary = "이미지 다중 업로드", description = "이미지를 업로드하고 해당 이미지의 URL 목록을 반환하는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
		@ApiResponse(responseCode = "500", description = "이미지 업로드 실패",
			content = @Content(mediaType = "application/json"))
	})
	@PostMapping(value = "/api/image/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<String>> uploadImages(
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		try {
			String basePath = rootPath + "/review";
			List<String> imageUrls = imageManagerService.uploadImages(files, basePath);
			return ResponseEntity.ok(imageUrls);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
