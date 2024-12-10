package shop.nuribooks.books.book.book.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;
import shop.nuribooks.books.book.category.dto.CategoryResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CacheController {
    private final CacheFeignClient cacheFeignClient;

    @GetMapping("/api/books/top/book-like")
    public ResponseEntity<List<TopBookResponse>> getTopBookLike() {
        List<TopBookResponse> response = cacheFeignClient.getTopBookLikes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/books/top/book-score")
    public ResponseEntity<List<TopBookResponse>> getTopBookScore() {
        List<TopBookResponse> response = cacheFeignClient.getTopBookScores();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/categories")
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categoryResponseList = cacheFeignClient.getAllCategory();
		return ResponseEntity.ok(categoryResponseList);
	}
}
