package shop.nuribooks.books.book.bookstate.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book_states")
public class BookState {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	private String detail;

	@Builder
	@Jacksonized
	private BookState(String detail) {
		this.detail = detail;
	}

	/**
	 * 기존 id는 유지하고 detail을 변경시키기위한 메서드
	 * 외부에서 id를 변경하지 못하게 하기위함
	 * @param newDetail 새로운 도서상태명
	 * @return 기존 id에 대한 detail만 변경된 새로운 BookState 객체
	 */
	public BookState updateDetail(String newDetail) {
		this.detail = newDetail;
		return this;
	}
}

