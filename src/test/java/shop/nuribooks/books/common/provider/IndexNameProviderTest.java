package shop.nuribooks.books.common.provider;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndexNameProviderTest {
	@InjectMocks
	IndexNameProvider indexNameProvider;

	@Test
	void resolveTest() {
		assertEquals(null, indexNameProvider.resolveIndexName());
	}
}
