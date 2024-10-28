package shop.nuribooks.books.exception.memberstate;

import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.member.authority.entity.AuthorityType;
import shop.nuribooks.books.member.state.entity.MemberStateType;

public class MemberStateAlreadyExistException extends ResourceAlreadyExistException {
	public MemberStateAlreadyExistException(MemberStateType memberStateType) {super(memberStateType.getValue() + "은 이미 존재합니다.");
	}
}
