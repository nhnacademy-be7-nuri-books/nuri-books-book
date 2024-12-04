package shop.nuribooks.books.order.refund.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.common.config.QuerydslConfiguration;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class RefundRepositoryTest {

	@DisplayName("")
	@Test
	void test() {
		// given

		// when

		// then
	}

}
