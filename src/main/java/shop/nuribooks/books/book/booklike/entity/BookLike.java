package shop.nuribooks.books.book.booklike.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.member.member.entity.Member;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "book_likes")
public class BookLike {
	@EmbeddedId
	private BookLikeId bookLikeId;

	@ManyToOne
	@JoinColumn(name = "member_id", insertable = false, updatable = false)
	private Member member;

	@ManyToOne
	@JoinColumn(name = "book_id", insertable = false, updatable = false)
	private Book book;
}
