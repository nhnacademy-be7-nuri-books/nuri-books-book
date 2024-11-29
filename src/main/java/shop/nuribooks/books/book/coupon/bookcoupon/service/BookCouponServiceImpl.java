package shop.nuribooks.books.book.coupon.bookcoupon.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.coupon.bookcoupon.dto.BookCouponRequest;
import shop.nuribooks.books.book.coupon.bookcoupon.dto.BookCouponResponse;
import shop.nuribooks.books.book.coupon.bookcoupon.entity.BookCoupon;
import shop.nuribooks.books.book.coupon.bookcoupon.repository.BookCouponRepository;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.coupon.BookCouponNotFoundException;

@RequiredArgsConstructor
@Service
public class BookCouponServiceImpl implements BookCouponService {
	private final BookRepository bookRepository;
	private final CouponRepository couponRepository;
	private final BookCouponRepository bookCouponRepository;

	@Transactional
	@Override
	public BookCoupon registerBookCoupon(BookCouponRequest request) {

		Coupon coupon = Coupon.builder()
			.name(request.name())
			.policyType(request.policyType())
			.discount(request.discount())
			.minimumOrderPrice(request.minimumOrderPrice())
			.maximumDiscountPrice(request.maximumDiscountPrice())
			.expirationType(request.expirationType())
			.expiredAt(request.expiredAt())
			.period(request.period())
			.couponType(request.couponType())
			.build();

		couponRepository.saveAndFlush(coupon);
		Book book = bookRepository.findById(request.bookId())
			.orElseThrow(() -> new BookNotFoundException(request.bookId()));

		BookCoupon bookCoupon = request.toEntity(coupon, book);
		return bookCouponRepository.save(bookCoupon);
	}

	@Override
	public BookCouponResponse getBookCoupon(Long id) {
		BookCoupon bookCoupon = bookCouponRepository.findByBookId(id)
			.orElseThrow(BookCouponNotFoundException::new);

		return new BookCouponResponse(bookCoupon);
	}

}
