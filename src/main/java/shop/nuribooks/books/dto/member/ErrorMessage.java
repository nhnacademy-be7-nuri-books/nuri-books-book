package shop.nuribooks.books.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {

	private int statusCode;
	private String message;
}
