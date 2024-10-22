package shop.nuribooks.books.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.Publishers;

public interface PublisherRepository extends JpaRepository<Publishers, Long> {
}
