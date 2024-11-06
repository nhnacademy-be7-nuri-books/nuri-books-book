package shop.nuribooks.books.book.bookcontributor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;

import java.util.List;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long>, BookContributorCustomRepository{
}
