package shop.nuribooks.books.exception.authority;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class AuthorityNotFoundException extends ResourceNotFoundException {
  public AuthorityNotFoundException(Long id) {
    super("해당하는 권한 ID " + id + " 를 찾을 수 없습니다.");
  }
}
