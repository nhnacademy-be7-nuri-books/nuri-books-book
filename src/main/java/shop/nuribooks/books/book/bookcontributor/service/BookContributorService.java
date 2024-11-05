package shop.nuribooks.books.book.bookcontributor.service;

import shop.nuribooks.books.book.bookcontributor.dto.BookContributorRegisterRequest;

public interface BookContributorService {
    void registerContributorToBook(BookContributorRegisterRequest registerRequest);
}
