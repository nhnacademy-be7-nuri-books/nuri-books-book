package shop.nuribooks.books.book.coupon.entity;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import shop.nuribooks.books.book.book.entity.Book;

@Entity
@DiscriminatorValue("BOOK")
@Getter
@NoArgsConstructor
@SuperBuilder
@Table(name = "book_coupons")
public class BookCoupon extends Coupon {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Override
	public boolean isApplicable(List<Long> idList) {
		return false;
	}
}
