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
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.repository.CouponPolicyRepository;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.coupon.BookCouponNotFoundException;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;

@RequiredArgsConstructor
@Service
public class BookCouponServiceImpl implements BookCouponService {
	private final BookRepository bookRepository;
	private final CouponRepository couponRepository;
	private final BookCouponRepository bookCouponRepository;
	private final CouponPolicyRepository couponPolicyRepository;

	@Transactional
	@Override
	public BookCoupon registerBookCoupon(BookCouponRequest request) {
		CouponPolicy couponPolicy = couponPolicyRepository.findById(request.couponPolicyId()).orElseThrow(
			CouponNotFoundException::new);

		Coupon coupon = Coupon.builder()
			.name(request.name())
			.couponPolicy(couponPolicy)
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
