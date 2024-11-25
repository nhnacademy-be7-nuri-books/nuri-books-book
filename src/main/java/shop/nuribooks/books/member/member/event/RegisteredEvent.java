package shop.nuribooks.books.member.member.event;

import lombok.AllArgsConstructor;
import shop.nuribooks.books.member.member.entity.Member;

@AllArgsConstructor
public class RegisteredEvent {
	private Member member;

	public Member getMember() {
		return this.member;
	}
}
