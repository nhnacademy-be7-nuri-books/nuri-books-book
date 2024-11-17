package shop.nuribooks.books.common.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IndexNameProvider {
	@Value("${spring.elasticsearch.index}")
	private String baseIndexName;

	public String resolveIndexName() { // 날짜 기반 접미사 생성 예시
		return baseIndexName;
	}
}