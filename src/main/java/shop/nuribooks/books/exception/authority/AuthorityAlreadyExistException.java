package shop.nuribooks.books.exception.authority;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.member.authority.entity.AuthorityEnum;

public class AuthorityAlreadyExistException extends ResourceAlreadyExistException {
	public AuthorityAlreadyExistException(AuthorityEnum authorityEnum) {super(authorityEnum.getValue() + "은 이미 존재합니다.");
	}
}
