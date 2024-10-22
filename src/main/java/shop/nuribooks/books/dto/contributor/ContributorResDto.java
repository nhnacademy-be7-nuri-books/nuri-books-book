package shop.nuribooks.books.dto.contributor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContributorResDto {
	private Long id;
	private String name;

	public ContributorResDto(String name) {
		this.name = name;
	}

}

