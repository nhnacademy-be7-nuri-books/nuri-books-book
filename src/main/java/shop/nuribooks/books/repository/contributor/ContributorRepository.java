package shop.nuribooks.books.repository.contributor;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.Contributors;

public interface ContributorRepository extends JpaRepository<Contributors, Long> {
}

