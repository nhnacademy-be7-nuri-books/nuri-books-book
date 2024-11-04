package shop.nuribooks.books.book.booktag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.booktag.entity.BookTag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
}
