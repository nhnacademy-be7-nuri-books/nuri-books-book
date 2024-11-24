package shop.nuribooks.books.book.booklike.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookLikeId implements Serializable {
	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "book_id")
	private Long bookId;
}
