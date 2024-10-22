package shop.nuribooks.books.service.member;

import static java.math.BigDecimal.*;
import static shop.nuribooks.books.entity.member.AuthorityEnum.*;
import static shop.nuribooks.books.entity.member.GradeEnum.*;
import static shop.nuribooks.books.entity.member.StatusEnum.*;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.request.MemberCreateReq;
import shop.nuribooks.books.dto.member.request.MemberWithdrawReq;
import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.entity.member.Member;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.InvalidPasswordException;
import shop.nuribooks.books.exception.member.UserIdAlreadyExistsException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.repository.member.CustomerRepository;
import shop.nuribooks.books.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final CustomerRepository customerRepository;
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * 고객(customer) 생성 후 customerRepository에 저장 <br>
	 * 그 후, 회원(member) 생성 후 memberRepository에 저장
	 * @throws EmailAlreadyExistsException 이미 존재하는 이메일입니다.
	 * @throws UserIdAlreadyExistsException 이미 존재하는 아이디입니다.
	 * @param request
	 * MemberCreateReq로 name, userId, password, phoneNumber, email, birthday를 받는다. <br>
	 * 생성일자(createdAt)는 현재 시간, point와 totalPaymentAmount는 0으로 초기화 <br>
	 * 권한은 MEMBER, 등급은 STANDARD, 상태는 ACTIVE로 초기화 <br>
	 * password는 BCryptPasswordEncoder로 해싱
	 */
	@Transactional
	public void createMember(MemberCreateReq request) {
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
			savedCustomer, MEMBER, STANDARD, ACTIVE, request.getUserId(),
			request.getBirthday(), LocalDateTime.now(), ZERO, ZERO);

		memberRepository.save(newMember);
	}

	/**
	 * 입력받은 아이디로 회원 존재 여부 확인
	 * @param userId
	 * @return 존재 여부 반환
	 */
	public boolean doesMemberExist(String userId) {
		return memberRepository.existsByUserId(userId);
	}

	/**
	 * 아이디와 비밀번호로 회원 검증 후 회원을 휴면 상태로 전환 <br>
	 * @throws UserIdNotFoundException 존재하지 않는 아이디입니다.
	 * @throws InvalidPasswordException 비밀번호가 일치하지 않습니다.
	 * @param request
	 * MemberResignReq로 아이디와 비밀번호를 받아서 확인 <br>
	 * 아이디로 member 존재 여부 먼저 확인하고, <br>
	 * member가 존재한다면 member의 PK인 id와 비밀번호 두 가지 값으로 customer 존재 여부 확인 <br>
	 * customer까지 존재한다면 마지막으로 member의 status를 INACTIVE로, <br>
	 * 탈퇴 일시인 withdrawnAt을 현재 시간으로 변경
	 */
	@Transactional
	public void withdrawMember(MemberWithdrawReq request) {
		if (!memberRepository.existsByUserId(request.getUserId())) {
			throw new UserIdNotFoundException("존재하지 않는 아이디입니다.");
		}

		Member findMember = memberRepository.findByUserId(request.getUserId());

		if (!customerRepository.existsByIdAndPassword(
			findMember.getId(), bCryptPasswordEncoder.encode(request.getPassword()))) {
			throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
		}

		findMember.changeStatus(INACTIVE);
		findMember.changeWithdrawnAt(LocalDateTime.now());
	}
}
