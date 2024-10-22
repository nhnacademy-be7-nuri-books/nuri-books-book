package shop.nuribooks.books.service.member;

import static java.math.BigDecimal.*;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.request.MemberCreateReq;
import shop.nuribooks.books.dto.member.request.MemberRemoveReq;
import shop.nuribooks.books.entity.member.AuthorityEnum;
import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.entity.member.GradeEnum;
import shop.nuribooks.books.entity.member.Member;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.InvalidPasswordException;
import shop.nuribooks.books.exception.member.UserIdAlreadyExistsException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.repository.member.CustomerRepository;
import shop.nuribooks.books.repository.member.MemberRepository;
import shop.nuribooks.books.repository.member.ResignedMemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final CustomerRepository customerRepository;
	private final MemberRepository memberRepository;
	private final ResignedMemberRepository resignedMemberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * 고객(customer) 생성 후 customerRepository에 저장 <br>
	 * 그 후, 회원(member) 생성 후 memberRepository에 저장
	 * @throws EmailAlreadyExistsException 이미 존재하는 이메일입니다.
	 * @throws UserIdAlreadyExistsException 이미 존재하는 아이디입니다.
	 * @param request
	 * MemberCreateRequest로 name, userId, password, phoneNumber, email, birthday를 받는다. <br>
	 * 생성일자(createdAt)는 현재 시간, point와 totalPaymentAmount는 0으로 초기화 <br>
	 * 권한은 MEMBER, 등급은 STANDARD로 초기화 <br>
	 * password는 BCryptPasswordEncoder로 해싱
	 */
	@Transactional
	public void memberCreate(MemberCreateReq request) {
		if (customerRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}
		if (memberRepository.existsByUserId(request.getUserId())) {
			throw new UserIdAlreadyExistsException("이미 존재하는 아이디입니다.");
		}

		Customer newCustomer = new Customer(
			request.getName(),
			bCryptPasswordEncoder.encode(request.getPassword()),
			request.getPhoneNumber(),
			request.getEmail());

		Customer savedCustomer = customerRepository.save(newCustomer);

		Member newMember = new Member(
			savedCustomer, AuthorityEnum.MEMBER, GradeEnum.STANDARD, request.getUserId(),
			request.getBirthday(), LocalDateTime.now(), ZERO, ZERO);

		memberRepository.save(newMember);
	}

	/**
	 * 입력받은 아이디로 회원 존재 여부 확인
	 * @param userId
	 * @return 존재 여부 반환
	 */
	public boolean isMemberExist(String userId) {
		return memberRepository.existsByUserId(userId);
	}

	public void memberRemove(MemberRemoveReq request) {
		if (!memberRepository.existsByUserId(request.getUserId())) {
			throw new UserIdNotFoundException("존재하지 않는 아이디입니다.");
		}
		if (!customerRepository.existsByPassword(bCryptPasswordEncoder.encode(request.getPassword()))) {
			throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
		}

	}
}
