package shop.nuribooks.books.exception.authority;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.member.authority.entity.AuthorityType;

public class AuthorityAlreadyExistException extends ResourceAlreadyExistException {
	public AuthorityAlreadyExistException(AuthorityType authorityType) {super(authorityType.getValue() + "은 이미 존재합니다.");
	}
}
