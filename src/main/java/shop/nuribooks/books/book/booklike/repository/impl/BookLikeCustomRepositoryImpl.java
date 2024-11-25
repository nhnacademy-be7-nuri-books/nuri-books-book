package shop.nuribooks.books.book.booklike.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.QBook;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.entity.QBookLike;
import shop.nuribooks.books.book.booklike.repository.BookLikeCustomRepository;
import shop.nuribooks.books.book.publisher.entity.QPublisher;

@RequiredArgsConstructor
public class BookLikeCustomRepositoryImpl implements BookLikeCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<BookLikeResponse> findLikedBooks(Long memberId, Pageable pageable) {
		QBookLike bookLike = QBookLike.bookLike;
		QBook book = QBook.book;
		QPublisher publisher = QPublisher.publisher;

		List<BookLikeResponse> likeResponses = queryFactory
			.select(Projections.constructor(
				BookLikeResponse.class,
				book.id,
				book.title,
				null,
				book.publisherId.name,
				book.price,
				book.discountRate,
				null,
				book.thumbnailImageUrl
			))
			.from(bookLike)
			.join(bookLike.book, book)
			.join(book.publisherId, publisher)
			.where(bookLike.bookLikeId.memberId.eq(memberId).and(book.deletedAt.isNull()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = Optional.ofNullable(queryFactory
				.select(bookLike.count())
				.from(bookLike)
				.where(bookLike.bookLikeId.memberId.eq(memberId).and(book.deletedAt.isNull()))
				.fetchOne())
			.orElse(0L);

		return new PageImpl<>(likeResponses, pageable, total);
	}
}
