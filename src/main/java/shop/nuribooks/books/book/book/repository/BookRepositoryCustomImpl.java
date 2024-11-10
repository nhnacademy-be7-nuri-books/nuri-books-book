package shop.nuribooks.books.book.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.QBook;
import shop.nuribooks.books.book.publisher.entity.QPublisher;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Book> findAllWithPublisher(Pageable pageable) {
		QBook book = QBook.book;
		QPublisher publisher = QPublisher.publisher;

		List<Book> books = queryFactory.selectFrom(book)
			.join(book.publisherId, publisher).fetchJoin()
			.where(book.deletedAt.isNull())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = Optional.ofNullable(queryFactory.select(book.count())
			.from(book)
			.join(book.publisherId, publisher)
				.where(book.deletedAt.isNull())
			.fetchOne())
			.orElse(0L);

		return new PageImpl<>(books, pageable, total);
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
}
