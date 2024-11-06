package shop.nuribooks.books.book.bookcontributor.repository;

import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;

import java.util.List;

public interface BookContributorCustomRepository {
    List<Long> findBookIdsByContributorId(Long contributorId);
    List<BookContributorInfoResponse> findContributorsAndRolesByBookId(Long bookId);

}
