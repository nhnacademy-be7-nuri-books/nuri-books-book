package shop.nuribooks.books.book.book.cache;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;
import shop.nuribooks.books.book.category.dto.CategoryResponse;

import java.util.List;

@FeignClient(name = "cacheFeignClient", url = "http://192.168.0.60:8089/api/cache")
public interface CacheFeignClient {
    @GetMapping("/categories")
    List<CategoryResponse> getAllCategory();

    @GetMapping("/books/top/book-like")
    List<TopBookResponse> getTopBookLikes();

    @GetMapping("/books/top/book-score")
    List<TopBookResponse> getTopBookScores();
}
