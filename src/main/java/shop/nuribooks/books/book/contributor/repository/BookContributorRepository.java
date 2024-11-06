package shop.nuribooks.books.book.contributor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.contributor.entity.BookContributor;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long> {
	List<BookContributor> findByBook(Book book);
}
