package shop.nuribooks.books.book.contributor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.contributor.entitiy.Contributor;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {
}

