package shop.nuribooks.books.book.bookcontributor.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.QBookContributor;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.entity.QContributor;
import shop.nuribooks.books.book.contributor.entity.QContributorRole;

@Repository
@RequiredArgsConstructor
public class BookContributorRepositoryImpl implements BookContributorCustomRepository {
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
				contributorRole.name.stringValue()
			))
			.from(bookContributor)
			.join(bookContributor.contributor, contributor)
			.join(bookContributor.contributorRole, contributorRole)
			.where(bookContributor.book.id.eq(bookId))
			.fetch()
			.stream()
			.map(contributorInfo -> {
				String roleName = contributorInfo.contributorRoleName();
				ContributorRoleEnum roleEnum = ContributorRoleEnum.fromString(roleName);
				return BookContributorInfoResponse.of(
					contributorInfo.contributorId(),
					contributorInfo.contributorName(),
					contributorInfo.contributorRoleId(),
					roleEnum.getKorName()
				);
			})
			.collect(Collectors.toList());
	}
}
