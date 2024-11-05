package shop.nuribooks.books.book.review.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.repository.ReviewRepository;
import shop.nuribooks.books.book.review.service.ReviewService;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.review.ReviewNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;
	private final ReviewRepository reviewRepository;

	/**
	 * 리뷰 등록. 리뷰 이미지도 함께 등록합니다.
	 * @param reviewRequest
	 * @param memberId
	 * @return
	 */
	@Override
	public ReviewMemberResponse registerReview(ReviewRequest reviewRequest, long memberId) {

		Member member = this.memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("등록되지 않은 유저입니다."));
		Book book = this.bookRepository.findById(reviewRequest.bookId())
			.orElseThrow(() -> new BookIdNotFoundException());

		Review review = reviewRequest.toEntity(member, book);
		Review result = this.reviewRepository.save(review);

		return ReviewMemberResponse.of(result);
	}

	/**
	 * review update
	 * @param reviewRequest
	 * @param memberId
	 * @return
	 */
	@Override
	public ReviewMemberResponse updateReview(ReviewRequest reviewRequest, long reviewId, long memberId) {
		// 기존 review update 처리
		Review prevReview = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException());
		prevReview.updated();
		reviewRepository.save(prevReview);

		Review newReview = reviewRequest.toEntity(prevReview.getMember(), prevReview.getBook());
		Review result = reviewRepository.save(newReview);
		return ReviewMemberResponse.of(result);
	}
}
