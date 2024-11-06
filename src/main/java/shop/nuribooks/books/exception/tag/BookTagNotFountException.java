package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class BookTagNotFountException extends ResourceNotFoundException {
    public BookTagNotFountException(String message) {
        super(message);
    }
}
