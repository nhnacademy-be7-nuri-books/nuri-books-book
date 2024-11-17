package shop.nuribooks.books.book.elasticsearch.docs;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.publisher.entity.Publisher;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "#{indexNameProperty.resolveIndexName()}")
@Setting(settingPath = "/elasticsearch/settings/nori-settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/nori-mappings.json")
@Builder
public class BookDocument {

	@Id
	@Field(type = FieldType.Long)
	private Long id;

	@Field(name = "publisher_name", type = FieldType.Keyword)
	private String publisherName;

	@Field(name = "state", type = FieldType.Keyword)
	private String state;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String title;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String description;

	@Field(type = FieldType.Double)
	private BigDecimal price;

	@Field(name = "sale_price", type = FieldType.Double)
	private BigDecimal salePrice;

	@Field(name = "discount_rate", type = FieldType.Integer)
	private int discountRate;

	@Field(name = "thumbnail_image_url", type = FieldType.Text, index = false)
	private String thumbnailImageUrl;

	// @Field(type = FieldType.Nested) // Nested 타입으로 설정
	// private List<ContributorDocument> contributors;

	public static BookDocument of(Book book, Publisher publisher) {
		return BookDocument.builder()
			.id(book.getId())
			.publisherName(publisher.getName())
			.state(book.getState().getKorName())
			.title(book.getTitle())
			.description(book.getDescription())
			.price(book.getPrice())
			.salePrice(book.getPrice()
				.divide(BigDecimal.valueOf(100))
				.multiply(BigDecimal.valueOf(100 - book.getDiscountRate())))
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.build();
	}
}