package shop.nuribooks.books.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {

	private int statusCode;
	private String message;
}
