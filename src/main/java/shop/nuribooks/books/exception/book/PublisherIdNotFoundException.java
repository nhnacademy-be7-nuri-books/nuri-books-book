package shop.nuribooks.books.exception.book;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class PublisherIdNotFoundException extends ResourceNotFoundException {
  public PublisherIdNotFoundException(Long id) {
    super("해당하는 출판사 ID " + id + " 를 찾을 수 없습니다.");
  }
}
