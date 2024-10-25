package shop.nuribooks.books.exception.authority;

import shop.nuribooks.books.exception.BadRequestException;

public class InvalidAuthorityEnumException extends BadRequestException {
    public InvalidAuthorityEnumException() {
        super("잘못된 Authority Type 입니다.");
    }
}
