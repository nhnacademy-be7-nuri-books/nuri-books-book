package shop.nuribooks.books.book.review.service.impl;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.review.dto.request.ReviewRegisterRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBreifResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.repository.ReviewRepository;
import shop.nuribooks.books.book.review.service.ReviewService;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;
	private final ReviewRepository reviewRepository;

	@Override
	public ReviewBreifResponse registerReview(ReviewRegisterRequest reviewRegisterRequest, long memberId) {
		Member member = this.memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("등록되지 않은 유저입니다."));
		Book book = this.bookRepository.findById(reviewRegisterRequest.bookId())
			.orElseThrow(() -> new BookIdNotFoundException());

		Review review = reviewRegisterRequest.toEntity(member, book);
		Review result = this.reviewRepository.save(review);

		return ReviewBreifResponse.of(result);
	}
}
