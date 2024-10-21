package shop.nuribooks.books.repository.books;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.Publishers;

public interface PublishersRepository extends JpaRepository<Publishers, Long> {
}
