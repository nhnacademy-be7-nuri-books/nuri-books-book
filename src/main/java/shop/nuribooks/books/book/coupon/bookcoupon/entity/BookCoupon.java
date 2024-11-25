package shop.nuribooks.books.book.coupon.bookcoupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.coupon.entity.Coupon;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "book_coupons")
public class BookCoupon {
	@Id
	@Column(name = "coupon_id")
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

}
