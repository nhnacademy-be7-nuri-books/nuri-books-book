package shop.nuribooks.books.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
