package shop.nuribooks.books.dto.contributor;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContributorReq {
	@Length(min = 1, max = 50)
	@NotNull(message = "Name cannot be null")
	private String name;
}