package shop.nuribooks.books.book.booktag.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.booktag.entity.QBookTag;
import shop.nuribooks.books.book.tag.entity.QTag;

@RequiredArgsConstructor
public class BookTagRepositoryImpl implements BookTagCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<String> findTagNamesByBookId(Long bookId) {
		QBookTag bookTag = QBookTag.bookTag;
		QTag tag = QTag.tag;

		return queryFactory
			.select(tag.name)
			.from(bookTag)
			.join(bookTag.tag, tag)
			.where(bookTag.book.id.eq(bookId))
			.fetch();
	}

	@Override
	public List<Long> findBookIdsByTagId(Long tagId) {
		QBookTag bookTag = QBookTag.bookTag;
		QTag tag = QTag.tag;

		return queryFactory
			.select(bookTag.book.id)
			.from(bookTag)
			.where(bookTag.tag.id.eq(tagId))
			.fetch();
	}
}
