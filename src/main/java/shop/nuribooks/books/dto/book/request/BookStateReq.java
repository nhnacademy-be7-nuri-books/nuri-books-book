package shop.nuribooks.books.dto.book.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.entity.book.enums.BookStatesEnum;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookStateReq {
	@NotNull
	private BookStatesEnum detail;
}
