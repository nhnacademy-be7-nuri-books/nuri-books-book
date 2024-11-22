package shop.nuribooks.books.book.bookcontributor.service;

import java.util.List;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorRegisterRequest;

public interface BookContributorService {
	void registerContributorToBook(BookContributorRegisterRequest registerRequest);

	List<BookResponse> getAllBooksByContributorId(Long contributorId);

	List<BookContributorInfoResponse> getContributorsAndRolesByBookId(Long bookId);

	void deleteBookContributor(Long bookContributorId);
}
