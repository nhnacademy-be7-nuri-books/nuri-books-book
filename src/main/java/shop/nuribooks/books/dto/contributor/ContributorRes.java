package shop.nuribooks.books.dto.contributor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContributorRes {
	private Long id;
	private String name;

	public ContributorRes(String name) {
		this.name = name;
	}

}

