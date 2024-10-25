package shop.nuribooks.books.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseMessage {

	private Integer statusCode;
	private String message;
}
