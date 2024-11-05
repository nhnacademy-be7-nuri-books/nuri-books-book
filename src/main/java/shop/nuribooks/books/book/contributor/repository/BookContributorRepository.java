package shop.nuribooks.books.book.contributor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.contributor.entitiy.BookContributor;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long> {
}
