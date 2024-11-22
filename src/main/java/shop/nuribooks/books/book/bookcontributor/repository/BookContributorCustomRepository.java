package shop.nuribooks.books.book.bookcontributor.repository;

import java.util.List;

import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;

public interface BookContributorCustomRepository {
	List<Long> findBookIdsByContributorId(Long contributorId);

	List<BookContributorInfoResponse> findContributorsAndRolesByBookId(Long bookId);

}
