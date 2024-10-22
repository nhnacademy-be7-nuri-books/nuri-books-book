package shop.nuribooks.books.dto.book;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookRegisterRes {
	private Long id;
	private Long stateId;
	private Long publisherId;
	private String title;
	private String thumbnailImageUrl;
	private LocalDate publicationDate;
	private BigDecimal price;
	private int discountRate;
	private String description;
	private int stock;
}