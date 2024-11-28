package shop.nuribooks.books.member.member.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GenderTypeTest {

	@DisplayName("성별 enum을 String으로 변환")
	@Test
	void getValue() {
		assertThat(GenderType.MALE.getValue()).isEqualTo("MALE");
		assertThat(GenderType.FEMALE.getValue()).isEqualTo("FEMALE");
	}

	@DisplayName("유효한 성별 enum 입력")
	@Test
	void fromValue_ValidInput() {
		assertThat(GenderType.fromValue("MALE")).isEqualTo(GenderType.MALE);
		assertThat(GenderType.fromValue("FEMALE")).isEqualTo(GenderType.FEMALE);
	}

	@DisplayName("유효한 enum 입력 - 소문자를 대문자로 변환")
	@Test
	void fromValue_UpperCaseInput() {
		assertThat(GenderType.fromValue("male")).isEqualTo(GenderType.MALE);
		assertThat(GenderType.fromValue("female")).isEqualTo(GenderType.FEMALE);
	}

	@DisplayName("유효하지 않은 enum 입력 - 존재하지 않는 성별")
	@Test
	void fromValue_InvalidInput() {
		assertThat(GenderType.fromValue("UNKNOWN")).isNull();
	}

	@DisplayName("유효하지 않은 enum 입력 - blank")
	@Test
	void fromValue_EmptyInput() {
		assertThat(GenderType.fromValue("  ")).isNull();
	}
}
