package shop.nuribooks.books.book.booklike.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.repository.BookLikeRepository;
import shop.nuribooks.books.book.booklike.service.BookLikeService;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.InvalidPageRequestException;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookLikeServiceImpl implements BookLikeService {

	private final BookLikeRepository bookLikeRepository;
	private final BookContributorRepository bookContributorRepository;

	@Override
	public void addLike(Long memberId, Long bookId) {

	}

	@Override
	public void removeLike(Long memberId, Long bookId) {

	}

	@Override
	public PagedResponse<BookLikeResponse> getLikedBooks(Long memberId, Pageable pageable) {
		if (pageable.getPageNumber() < 0) {
			throw new InvalidPageRequestException();
		}

		Page<BookLikeResponse> bookLikePage = bookLikeRepository.findLikedBooks(memberId, pageable);

		List<BookLikeResponse> bookLikeResponses = bookLikePage.getContent().stream()
			.map(bookLike -> {
				List<BookContributorInfoResponse> contributors =
					bookContributorRepository.findContributorsAndRolesByBookId(bookLike.bookId());
				Map<String, List<String>> contributorsByRole = BookUtils.groupContributorsByRole(contributors);

				return bookLike.toBuilder()
					.contributorsByRole(contributorsByRole)
					.salePrice(BookUtils.calculateSalePrice(bookLike.price(), bookLike.discountRate()))
					.build();
			})
			.toList();

		return PagedResponse.of(
			bookLikeResponses,
			pageable,
			(int)bookLikePage.getTotalElements()
		);
	}
}
