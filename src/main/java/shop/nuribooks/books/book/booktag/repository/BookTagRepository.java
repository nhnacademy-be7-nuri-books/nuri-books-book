package shop.nuribooks.books.book.booktag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.booktag.entity.BookTag;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long>, BookTagCustomRepository {
    List<BookTag> findByBookId(Long bookId);

    boolean existsByBookIdAndTagId(Long bookId, Long tagId);
}
