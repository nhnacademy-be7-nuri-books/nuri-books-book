package shop.nuribooks.books.dto.contributor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContributorResponse {
	private Long id;
	private String name;

	public ContributorResponse(String name) {
		this.name = name;
	}

}

