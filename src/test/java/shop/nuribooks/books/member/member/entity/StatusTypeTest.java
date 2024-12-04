package shop.nuribooks.books.member.member.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusTypeTest {

	@DisplayName("상태 enum을 String으로 변환")
	@Test
	void getValue() {
		assertThat(StatusType.ACTIVE.getValue()).isEqualTo("ACTIVE");
		assertThat(StatusType.INACTIVE.getValue()).isEqualTo("INACTIVE");
		assertThat(StatusType.WITHDRAWN.getValue()).isEqualTo("WITHDRAWN");
	}

	@DisplayName("유효한 상태 enum 입력")
	@Test
	void fromValue_ValidInput() {
		assertThat(StatusType.fromValue("ACTIVE")).isEqualTo(StatusType.ACTIVE);
		assertThat(StatusType.fromValue("INACTIVE")).isEqualTo(StatusType.INACTIVE);
		assertThat(StatusType.fromValue("WITHDRAWN")).isEqualTo(StatusType.WITHDRAWN);
	}

	@DisplayName("유효한 enum 입력 - 소문자를 대문자로 변환")
	@Test
	void fromValue_UpperCaseInput() {
		assertThat(StatusType.fromValue("active")).isEqualTo(StatusType.ACTIVE);
		assertThat(StatusType.fromValue("inactive")).isEqualTo(StatusType.INACTIVE);
		assertThat(StatusType.fromValue("withdrawn")).isEqualTo(StatusType.WITHDRAWN);
	}

	@DisplayName("유효하지 않은 enum 입력 - 존재하지 않는 상태")
	@Test
	void fromValue_InvalidInput() {
		assertThat(StatusType.fromValue("UNKNOWN")).isNull();
	}

	@DisplayName("유효하지 않은 enum 입력 - blank")
	@Test
	void fromValue_EmptyInput() {
		assertThat(StatusType.fromValue("  ")).isNull();
	}
}
