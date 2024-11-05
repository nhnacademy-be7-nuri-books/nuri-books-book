package shop.nuribooks.books.book.bookcontributor.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.QBookContributor;
import shop.nuribooks.books.book.contributor.entitiy.QContributor;
import shop.nuribooks.books.book.contributor.entitiy.QContributorRole;


import java.util.List;

@RequiredArgsConstructor
public class BookContributorRepositoryImpl implements BookContributorCustomRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findBookIdsByContributorId(Long contributorId) {
        QBookContributor bookContributor = QBookContributor.bookContributor;

        return queryFactory.select(bookContributor.book.id)
                .from(bookContributor)
                .where(bookContributor.contributor.id.eq(contributorId))
                .fetch();
    }

    @Override
    public List<BookContributorInfoResponse> findContributorsAndRolesByBookId(Long bookId) {
        QBookContributor bookContributor = QBookContributor.bookContributor;
        QContributor contributor = QContributor.contributor;
        QContributorRole contributorRole = QContributorRole.contributorRole;

        return queryFactory.select(Projections.constructor(
                        BookContributorInfoResponse.class,
                        contributor.id,
                        contributor.name,
                        contributorRole.id,
                        contributorRole.name))
                .from(bookContributor)
                .join(bookContributor.contributor, contributor)
                .join(bookContributor.contributorRole, contributorRole)
                .where(bookContributor.book.id.eq(bookId))
                .fetch();
    }
}
