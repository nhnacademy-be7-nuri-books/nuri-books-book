package shop.nuribooks.books.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseMessage {

	private Integer statusCode;
	private String message;
}
