package shop.nuribooks.books.dto.contributorrole;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContributorRoleReq {
	@Column(length = 50, nullable = false)
	private String name;
}

