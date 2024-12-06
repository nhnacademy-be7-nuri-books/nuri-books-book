package shop.nuribooks.books.member.member.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthorityTypeTest {

	@DisplayName("권한 enum을 String으로 변환")
	@Test
	void getValue() {
		assertThat(AuthorityType.ADMIN.getValue()).isEqualTo("ADMIN");
		assertThat(AuthorityType.MEMBER.getValue()).isEqualTo("MEMBER");
		assertThat(AuthorityType.SELLER.getValue()).isEqualTo("SELLER");
	}

	@DisplayName("유효한 권한 enum 입력")
	@Test
	void fromValue_ValidInput() {
		assertThat(AuthorityType.fromValue("ADMIN")).isEqualTo(AuthorityType.ADMIN);
		assertThat(AuthorityType.fromValue("MEMBER")).isEqualTo(AuthorityType.MEMBER);
		assertThat(AuthorityType.fromValue("SELLER")).isEqualTo(AuthorityType.SELLER);
	}

	@DisplayName("유효한 enum 입력 - 소문자를 대문자로 변환")
	@Test
	void fromValue_UpperCaseInput() {
		assertThat(AuthorityType.fromValue("admin")).isEqualTo(AuthorityType.ADMIN);
		assertThat(AuthorityType.fromValue("member")).isEqualTo(AuthorityType.MEMBER);
		assertThat(AuthorityType.fromValue("seller")).isEqualTo(AuthorityType.SELLER);
	}

	@DisplayName("유효하지 않은 enum 입력 - 존재하지 않는 권한")
	@Test
	void fromValue_InvalidInput() {
		assertThat(AuthorityType.fromValue("UNKNOWN")).isNull();
	}

	@DisplayName("유효하지 않은 enum 입력 - blank")
	@Test
	void fromValue_EmptyInput() {
		assertThat(AuthorityType.fromValue("  ")).isNull();
	}

	@DisplayName("유효하지 않은 enum 입력 - null")
	@Test
	void fromValue_NullInput() {
		assertThat(AuthorityType.fromValue(null)).isNull();
	}
}
