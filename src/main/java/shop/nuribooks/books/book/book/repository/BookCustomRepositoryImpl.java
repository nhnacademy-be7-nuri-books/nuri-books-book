package shop.nuribooks.books.book.book.repository;

import static shop.nuribooks.books.book.book.entity.QBook.*;
import static shop.nuribooks.books.book.publisher.entity.QPublisher.*;
import static shop.nuribooks.books.book.review.entity.QReview.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookListResponse;
import shop.nuribooks.books.book.book.dto.TopBookLikeResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.QBook;
import shop.nuribooks.books.book.book.enums.SortType;
import shop.nuribooks.books.book.publisher.entity.QPublisher;

@Repository
@RequiredArgsConstructor
public class BookCustomRepositoryImpl implements BookCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<BookListResponse> findAllWithPublisher(Pageable pageable) {
		QBook book = QBook.book;
		QPublisher publisher = QPublisher.publisher;

		JPAQuery<BookListResponse> query = queryFactory.select(
				Projections.constructor(
					BookListResponse.class,
					book.id,
					publisher.name,
					book.state,
					book.title,
					book.publicationDate,
					book.price,
					book.discountRate,
					book.thumbnailImageUrl
				)
			)
			.from(book)
			.join(book.publisherId, publisher)
			.leftJoin(review).on(review.book.id.eq(book.id))
			.where(book.deletedAt.isNull())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.groupBy(book.id);

		pageable.getSort().forEach(order -> {
			SortType type = SortType.convert(order.getProperty());
			OrderSpecifier orderSpecifier = new OrderSpecifier(type.getOrder(), type.getExpression());
			query.orderBy(orderSpecifier);
		});

		return query.fetch();
	}

	@Override
	public Optional<Book> findBookByIdAndDeletedAtIsNull(Long bookId) {
		QBook book = QBook.book;

		Book result = queryFactory.selectFrom(book)
			.where(book.id.eq(bookId)
				.and(book.deletedAt.isNull()))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<TopBookLikeResponse> findTopBooksByLikes() {
		QBook book = QBook.book;

		return queryFactory.select(Projections.constructor(
				TopBookLikeResponse.class,
				book.id,
				book.thumbnailImageUrl,
				book.title
			))
			.from(book)
			.where(book.deletedAt.isNull())
			.orderBy(book.likeCount.desc())
			.limit(10)
			.fetch();
	}

	@Override
	public List<Book> findAllAndDeletedAtIsNull() {
		QBook book = QBook.book;
		return queryFactory.selectFrom(book)
			.where(book.deletedAt.isNull())
			.fetch();
	}

	@Override
	public long countBook() {
		return Optional.ofNullable(
				queryFactory.select(book.count())
					.from(book)
					.join(book.publisherId, publisher)
					.where(book.deletedAt.isNull())
					.fetchOne())
			.orElse(0L);
	}
}
