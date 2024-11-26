package shop.nuribooks.books.book.elasticsearch.docs;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(indexName = "#{indexNameProperty.resolveIndexName()}")
public class BookDocument {

	@Id
	private Long id;

	private String publisher_name;

	private String state;

	private String title;

	private String description;

	private BigDecimal price;

	private BigDecimal sale_price;

	private int discount_rate;

	private String thumbnail_image_url;

	private long view_count;

	private int review_count;

	private BigDecimal total_score;

	private Date deleted_at;

	// localdatetime -> date로 타입 변경.
	// jackson이 Localdatetime 변환 지원 x
	// 추가 라이브러리가 elastic search의 object mapper에 적용안됨.
	private Date publication_date;

	private List<String> tags; // 태그 리스트

	private List<ContributorDocument> contributors; // 기여자 리스트
}
