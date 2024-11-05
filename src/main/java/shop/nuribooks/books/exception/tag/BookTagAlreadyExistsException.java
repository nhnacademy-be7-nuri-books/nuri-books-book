package shop.nuribooks.books.exception.tag;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class BookTagAlreadyExistsException extends ResourceAlreadyExistException {
    public BookTagAlreadyExistsException(String message) {
        super(message);
    }
}
