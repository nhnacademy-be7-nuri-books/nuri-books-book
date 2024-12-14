package shop.nuribooks.books.book.coupon.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.response.BookOrderResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.entity.BookCoupon;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
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
			.createdAt(LocalDate.now())
			.book(book)
			.build();
	}

	@Override
	public MemberCouponOrderDto isCouponApplicableToOrder(MemberCoupon memberCoupon,
		List<BookOrderResponse> bookOrderResponses) {

		if (memberCoupon.getCoupon() instanceof BookCoupon bookCoupon) {
			BigDecimal totalOrderPrice;
			List<Long> bookIds = new ArrayList<>();

			totalOrderPrice = calculateTotalOrderPrice(bookCoupon, bookOrderResponses, bookIds);

			if (totalOrderPrice.compareTo(bookCoupon.getCouponPolicy().getMinimumOrderPrice()) >= 0) {
				return MemberCouponOrderDto.toDto(memberCoupon, totalOrderPrice, bookIds);
			}
		}
		return null;

	}

	@Override
	public BigDecimal calculatePrice(MemberCoupon memberCoupon, List<BookOrderResponse> bookOrderResponses) {

		if (memberCoupon.getCoupon() instanceof BookCoupon bookCoupon) {
			BigDecimal totalOrderPrice;
			List<Long> bookIds = new ArrayList<>();

			totalOrderPrice = calculateTotalOrderPrice(bookCoupon, bookOrderResponses, bookIds);

			if (totalOrderPrice.compareTo(bookCoupon.getCouponPolicy().getMinimumOrderPrice()) >= 0) {
				return memberCoupon.getCoupon().getCouponPolicy().calculateDiscount(totalOrderPrice);
			}
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal calculateTotalOrderPrice(BookCoupon bookCoupon, List<BookOrderResponse> bookOrderResponses,
		List<Long> bookIds) {
		BigDecimal totalOrderPrice = BigDecimal.ZERO;
		for (BookOrderResponse bookOrderResponse : bookOrderResponses) {
			if (bookCoupon.getBook().getId().equals(bookOrderResponse.bookId())) {
				totalOrderPrice = totalOrderPrice.add(bookOrderResponse.bookTotalPrice());
				bookIds.add(bookOrderResponse.bookId());
			}
		}
		return totalOrderPrice;
	}
}
