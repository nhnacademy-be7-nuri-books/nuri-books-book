package shop.nuribooks.books.exception.memberstate;

import shop.nuribooks.books.exception.ResourceNotFoundException;

public class MemberStateNotFoundException extends ResourceNotFoundException {
  public MemberStateNotFoundException(Long id) {
    super("해당하는 상태 ID " + id + " 를 찾을 수 없습니다.");
  }
}
