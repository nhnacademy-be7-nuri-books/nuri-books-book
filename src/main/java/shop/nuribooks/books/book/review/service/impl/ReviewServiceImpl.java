package shop.nuribooks.books.book.review.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
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
	 * 도서의 평균 평점 반환
	 *
	 * @param bookId
	 * @return
	 */
	@Override
	public double getScoreByBookId(long bookId) {
		if (!bookRepository.existsById(bookId))
			throw new BookIdNotFoundException();
		return this.reviewRepository.findScoreByBookId(bookId);
	}

	/**
	 * 도서와 관련된 review 목록 반환
	 *
	 * @param bookId
	 * @return
	 */
	@Override
	public List<ReviewMemberResponse> getReviewsWithMember(long bookId) {
		if (!bookRepository.existsById(bookId))
			throw new BookIdNotFoundException();
		return this.reviewRepository.findReviewsByBookId(bookId);
	}

	/**
	 * 회원과 관련된 review 목록 반환
	 *
	 * @param memberId
	 * @return
	 */
	@Override
	public List<ReviewBookResponse> getReviewsWithBook(long memberId) {
		if (!memberRepository.existsById(memberId))
			throw new MemberNotFoundException("유저를 찾을 수 없습니다.");
		return this.reviewRepository.findReviewsByMemberId(memberId);
  }
  
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
