package shop.nuribooks.books.book.elasticsearch.docs;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "#{indexNameProperty.resolveIndexName()}")
public class BookDocument {

	@Id
	private Long id;

	private String publisherName;

	private String state;

	private String title;

	private String description;

	private BigDecimal price;

	private BigDecimal salePrice;

	private int discountRate;

	private String thumbnailImageUrl;

	private long viewCount;

	private int reviewCount;

	private BigDecimal totalScore;

	// localdatetime -> date로 타입 변경.
	// jackson이 Localdatetime 변환 지원 x
	// 추가 라이브러리가 elastic search의 object mapper에 적용안됨.
	private Date publication_date;

	private List<String> tags; // 태그 리스트

	private List<ContributorDocument> contributors; // 기여자 리스트
}
