package shop.nuribooks.books.book.booklike.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLikeId implements Serializable {
	private Long memberId;
	private Long bookId;
}
