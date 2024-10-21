package shop.nuribooks.books.dto.books;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BooksRegisterResDto {
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
