package shop.nuribooks.books.book.coupon.strategy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.entity.BookCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.exception.book.BookNotFoundException;

@Component
@RequiredArgsConstructor
public class BookCouponStrategy implements CouponStrategy {
	private final BookRepository bookRepository;

	@Override
	public Coupon registerCoupon(CouponRequest request, CouponPolicy couponPolicy) {
		Book book = bookRepository.findById(request.itemId())
			.orElseThrow(() -> new BookNotFoundException(request.itemId()));

		// BookCoupon 생성
		return BookCoupon.builder()
			.name(request.name())
			.couponType(request.couponType())
			.couponPolicy(couponPolicy) // CouponPolicy 매핑
			.expirationType(request.expirationType())
			.expiredAt(request.expiredAt())
			.period(request.period())
			.issuanceType(request.issuanceType())
			.quantity(request.quantity())
			.book(book)
			.build();
	}
}
