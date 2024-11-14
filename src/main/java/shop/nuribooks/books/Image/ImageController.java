package shop.nuribooks.books.Image;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageController {
	private final ImageManagerService imageManagerService;

	@PostMapping("/api/books/uploadImage")
	public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file) {
		try {
			String imageUrl = imageManagerService.uploadImage(file);
			return ResponseEntity.ok(imageUrl);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ImageUpload Fail");
		}
	}
}
