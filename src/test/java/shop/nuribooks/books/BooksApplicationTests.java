package shop.nuribooks.books;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.test.context.TestPropertySource;

@EnableDiscoveryClient
@SpringBootTest
@TestPropertySource(properties = {
	"NHN_KEY_MANAGER_SECRET_ACCESS_KEY=your_secret_key"
})
class BooksApplicationTests {

	@Test
	void contextLoads() {
	}

}
