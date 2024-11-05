package shop.nuribooks.books.book.contributor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.contributor.entitiy.Contributor;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {
	Optional<Contributor> findByName(String name);
	boolean existsByName(String name);
}

