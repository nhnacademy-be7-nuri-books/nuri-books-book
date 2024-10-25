package shop.nuribooks.books.member.member.service;

import static java.math.BigDecimal.*;
import static shop.nuribooks.books.member.authority.entity.AuthorityType.MEMBER;
import static shop.nuribooks.books.member.member.entity.GradeEnum.STANDARD;
import static shop.nuribooks.books.member.member.entity.StatusEnum.ACTIVE;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberWithdrawRequest;
import shop.nuribooks.books.member.member.dto.response.MemberCheckResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
import shop.nuribooks.books.member.member.entity.Customer;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.InvalidPasswordException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.UserIdAlreadyExistsException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.member.member.repository.CustomerRepository;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final CustomerRepository customerRepository;
	private final MemberRepository memberRepository;

	/**
	 * 회원등록 <br>
	 * 고객(customer) 생성 후 customerRepository에 저장 <br>
	 * 그 후, 회원(member) 생성 후 memberRepository에 저장
	 * @throws EmailAlreadyExistsException 이미 존재하는 이메일입니다.
	 * @throws UserIdAlreadyExistsException 이미 존재하는 아이디입니다.
	 * @param request
	 * MemberRegisterRequest로 name, userId, password, phoneNumber, email, birthday를 받는다. <br>
	 * 생성일자(createdAt)는 현재 시간, point와 totalPaymentAmount는 0으로 초기화 <br>
	 * 권한은 MEMBER, 등급은 STANDARD, 상태는 ACTIVE로 초기화 <br>
	 * @return MemberRegisterResponse에 이름, 유저 아이디, 전화번호, 이메일, 생일을 담아서 반환
	 */
	@Transactional
	public MemberRegisterResponse registerMember(MemberRegisterRequest request) {
		if (customerRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}
		if (memberRepository.existsByUserId(request.getUserId())) {
			throw new UserIdAlreadyExistsException("이미 존재하는 아이디입니다.");
		}

		Customer newCustomer = new Customer(
			request.getName(),
			request.getPassword(),
			request.getPhoneNumber(),
			request.getEmail());

		Customer savedCustomer = customerRepository.save(newCustomer);

		Member newMember = new Member(
			savedCustomer, MEMBER, STANDARD, ACTIVE, request.getUserId(),
			request.getBirthday(), LocalDateTime.now(), ZERO, ZERO);

		Member savedMember = memberRepository.save(newMember);

		return MemberRegisterResponse.builder()
			.name(savedCustomer.getName())
			.userId(savedMember.getUserId())
			.phoneNumber(savedCustomer.getPhoneNumber())
			.email(savedCustomer.getEmail())
			.birthday(savedMember.getBirthday())
			.build();
	}

	/**
	 * 회원탈퇴 <br>
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
	public void withdrawMember(MemberWithdrawRequest request) {
		Member findMember = memberRepository.findByUserId(request.getUserId())
			.orElseThrow(() -> new UserIdNotFoundException("존재하지 않는 아이디입니다."));

		if (!customerRepository.existsByIdAndPassword(
			findMember.getId(), request.getPassword())) {
			throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
		}

		findMember.changeToWithdrawn();
	}

	/**
	 * 회원 정보 수정 <br>
	 * 변경 가능한 정보는 name, password, phoneNumber
	 * @param userId  아이디
	 * @param request MemberUpdateRequest로 수정하고 싶은 이름과 비밀번호, 전화번호를 받음 <br>
	 * 로그인 상태의 사용자만이 회원 정보를 수정할 수 있기 때문에 <br>
	 * 입력받은 userId의 member가 반드시 존재하는 상황이다. <br>
	 * 그러므로 userId를 통해 customerRepository에서 해당 customer를 찾고 정보 수정을 진행 <br>
	 * 입력받은 각각의 이름, 비밀번호, 전화번호로 customer의 정보 수정
	 * @return MemberUpdateResponse에 변경한 이름과 전화번호 담아서 반환
	 */
	@Transactional
	public MemberUpdateResponse updateMember(String userId, MemberUpdateRequest request) {
		Member findMember = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		Customer findCustomer = customerRepository.findById(findMember.getId())
			.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

		findCustomer.changeCustomerInformation(
			request.getName(), request.getPassword(), request.getPhoneNumber());

		return MemberUpdateResponse.builder()
			.name(findCustomer.getName())
			.phoneNumber(findCustomer.getPhoneNumber())
			.build();
	}

	/**
	 * 입력받은 아이디로 회원의 이름, 비밀번호, 권한을 조회
	 * @param userId 아이디
	 * @return 회원이 존재하면 이름, 비밀번호, 권한을 MemberCheckResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 이름, 비밀번호, 권한이 모두 null인 MemberCheckResponse를 반환
	 */
	public MemberCheckResponse checkMember(String userId) {

		Member findMember = memberRepository.findByUserId(userId)
			.orElse(null);

		if (findMember == null) {
			return MemberCheckResponse.builder()
				.name(null)
				.password(null)
				.authority(null)
				.build();
		}

		Customer findCustomer = customerRepository.findById(findMember.getId())
			.orElse(null);

		if (findCustomer == null) {
			return MemberCheckResponse.builder()
				.name(null)
				.password(null)
				.authority(null)
				.build();
		}

		return MemberCheckResponse.builder()
			.name(findCustomer.getName())
			.password(findCustomer.getPassword())
			.authority("ROLE_" + findMember.getAuthority().name())
			.build();
	}
}
