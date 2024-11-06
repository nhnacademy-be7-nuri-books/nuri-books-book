package shop.nuribooks.books.book.booktag.repository;

import java.util.List;

public interface BookTagCustomRepository {
    List<String> findTagNamesByBookId(Long bookId);
    List<Long> findBookIdsByTagId(Long tagId);
}
