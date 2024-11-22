package shop.nuribooks.books.book.bookcontributor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;

public interface BookContributorRepository
	extends JpaRepository<BookContributor, Long>, BookContributorCustomRepository {
	List<BookContributor> findByContributorId(long contributorId);

	List<BookContributor> findByBookId(Long bookId);
}
