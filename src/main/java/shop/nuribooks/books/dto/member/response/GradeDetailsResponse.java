package shop.nuribooks.books.dto.member.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
class GradeDetailsResponse {

	private String name;
	private Integer pointRate;
	private BigDecimal requirement;

}
