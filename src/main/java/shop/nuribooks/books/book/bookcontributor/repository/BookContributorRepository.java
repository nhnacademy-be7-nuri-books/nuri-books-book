package shop.nuribooks.books.book.bookcontributor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long> {
}
