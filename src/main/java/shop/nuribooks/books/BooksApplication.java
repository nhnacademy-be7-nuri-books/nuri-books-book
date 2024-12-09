package shop.nuribooks.books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableFeignClients(basePackages = "shop.nuribooks.books.book.client")
public class BooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooksApplication.class, args);
	}

}
