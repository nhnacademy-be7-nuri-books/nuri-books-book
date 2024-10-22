package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.entity.book.enums.BookStatesEnum;
import shop.nuribooks.books.exception.IsAlreadyExistsException;

public class IsAlreadyExistsBookStateException extends IsAlreadyExistsException {
  public IsAlreadyExistsBookStateException(BookStatesEnum detail) {
    super("입력한 isbn " + detail + " 은 이미 존재합니다.");
  }
}
