package shop.nuribooks.books.book.review.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.review.dto.ReviewImageDto;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.repository.ReviewImageRepository;
import shop.nuribooks.books.book.review.repository.ReviewRepository;
import shop.nuribooks.books.book.review.service.ReviewService;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.review.ReviewNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewImageRepository reviewImageRepository;

	/**
	 * 리뷰 등록. 리뷰 이미지도 함께 등록합니다.
	 * @param reviewRequest
	 * @param ownerId
	 * @return
	 */
	@Override
	public ReviewMemberResponse registerReview(ReviewRequest reviewRequest, long ownerId) {

		Member member = this.memberRepository.findById(ownerId)
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
	public PagedResponse<ReviewMemberResponse> getReviewsByBookId(long bookId, Pageable pageable) {
		if (!bookRepository.existsById(bookId))
			throw new BookIdNotFoundException();
		// review만 가져오기
		List<ReviewMemberResponse> reviews = this.reviewRepository.findReviewsByBookId(bookId, pageable);
		// 리뷰가 있다면, 이미지 조합하기
		if (reviews.size() > 0) {
			// review id가 key인 map 생성 및 초기화
			Map<Long, ReviewMemberResponse> reviewMap = new LinkedHashMap<>();
			for (ReviewMemberResponse rmr : reviews) {
				reviewMap.put(rmr.id(), rmr);
			}

			// 리뷰 아이디별 리뷰 이미지 가져오기. query의 in을 사용하기 위해 리뷰 아이디의 리스트를 전달함.
			List<ReviewImageDto> reviewImages = this.reviewImageRepository.findReviewImagesByReviewIds(
				reviewMap.keySet().stream().toList());

			// 리뷰 이미지 목록을 리뷰에 넣어주기.
			for (ReviewImageDto rir : reviewImages) {
				reviewMap.get(rir.reviewId()).reviewImages().add(rir.reviewImageResponse());
			}
		}

		int totalElement = (int)this.reviewRepository.countByBookId(bookId);

		PagedResponse pagedResponse = PagedResponse.of(reviews, pageable, totalElement);

		return pagedResponse;
	}

	/**
	 * 회원과 관련된 review 목록 반환
	 *
	 * @param memberId
	 * @return
	 */
	@Override
	public PagedResponse<ReviewBookResponse> getReviewsByMemberId(long memberId, Pageable pageable) {
		if (!memberRepository.existsById(memberId))
			throw new MemberNotFoundException("유저를 찾을 수 없습니다.");
		// review만 가져오기
		List<ReviewBookResponse> reviews = this.reviewRepository.findReviewsByMemberId(memberId, pageable);

		// 리뷰가 있다면, 이미지 조합하기
		if (reviews.size() > 0) {
			// review id가 key인 map 생성 및 초기화
			Map<Long, ReviewBookResponse> reviewMap = new LinkedHashMap<>();
			for (ReviewBookResponse rbr : reviews) {
				reviewMap.put(rbr.id(), rbr);
			}

			// 리뷰 아이디별 리뷰 이미지 가져오기. query의 in을 사용하기 위해 리뷰 아이디의 리스트를 전달함.
			List<ReviewImageDto> reviewImages = this.reviewImageRepository.findReviewImagesByReviewIds(
				reviewMap.keySet().stream().toList());
			// 리뷰 이미지 목록을 리뷰에 넣어주기.
			for (ReviewImageDto rir : reviewImages) {
				reviewMap.get(rir.reviewId()).reviewImages().add(rir.reviewImageResponse());
			}
		}

		int totalElement = (int)this.reviewRepository.countByMemberId(memberId);

		PagedResponse pagedResponse = PagedResponse.of(reviews, pageable, totalElement);

		return pagedResponse;
	}

	@Transactional
	public ReviewMemberResponse updateReview(ReviewRequest reviewRequest, long reviewId, long ownerId) {
		// 기존 review update 처리
		Review prevReview = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException());
		if (prevReview.getMember().getId() != ownerId) {
			throw new ReviewNotFoundException();
		}
		prevReview.updated();

		Review newReview = reviewRequest.toEntity(prevReview.getMember(), prevReview.getBook());
		Review result = reviewRepository.save(newReview);
		return ReviewMemberResponse.of(result);
	}
}
