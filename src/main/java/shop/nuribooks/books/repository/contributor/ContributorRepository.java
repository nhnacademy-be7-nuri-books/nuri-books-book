package shop.nuribooks.books.repository.contributor;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.Contributor;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {
}

