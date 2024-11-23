package shop.nuribooks.books.book.booklike.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.entity.BookLike;
import shop.nuribooks.books.book.booklike.entity.BookLikeId;
import shop.nuribooks.books.book.booklike.repository.BookLikeRepository;
import shop.nuribooks.books.book.booklike.service.BookLikeService;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.booklike.ResourceAlreadyExistBookLikeIdException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookLikeServiceImpl implements BookLikeService {

	private final BookLikeRepository bookLikeRepository;
	private final BookContributorRepository bookContributorRepository;
	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;

	@Transactional
	@Override
	public void addLike(Long memberId, Long bookId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId));

		if (bookLikeRepository.existsById(new BookLikeId(memberId, bookId))) {
			throw new ResourceAlreadyExistBookLikeIdException();
		}

		book.incrementViewCount();

		bookLikeRepository.save(new BookLike(new BookLikeId(memberId, bookId), book));
	}

	@Override
	public void removeLike(Long memberId, Long bookId) {

	}

	@Override
	public PagedResponse<BookLikeResponse> getLikedBooks(Long memberId, Pageable pageable) {
		if (pageable.getPageNumber() < 0) {
			throw new InvalidPageRequestException();
		}

		memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

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
