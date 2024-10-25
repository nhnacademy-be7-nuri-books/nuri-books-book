package shop.nuribooks.books.book.publisher.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.publisher.entitiy.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
	boolean existsByName(String name);

	Optional<Publisher> findByName(String name);
}
