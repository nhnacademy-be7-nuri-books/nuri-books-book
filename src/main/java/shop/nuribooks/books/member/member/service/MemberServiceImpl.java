package shop.nuribooks.books.member.member.service;

import static shop.nuribooks.books.member.member.entity.AuthorityType.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.GradeNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.PasswordDuplicateException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.exception.member.UsernameAlreadyExistsException;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.DtoMapper;
import shop.nuribooks.books.member.member.dto.EntityMapper;
import shop.nuribooks.books.member.member.dto.request.MemberPasswordUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.event.RegisteredEvent;
import shop.nuribooks.books.member.member.repository.MemberRepository;

/**
 * @author Jprotection
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final CustomerRepository customerRepository;
	private final MemberRepository memberRepository;
	private final GradeRepository gradeRepository;
	private final CartRepository cartRepository;
	private final ApplicationEventPublisher publisher;

	/**
	 * 회원등록 <br>
	 * 고객(customer) 생성 후 customerRepository에 저장 <br>
	 * 그 후, 회원(member) 생성 후 memberRepository에 저장
	 *
	 * @param request MemberRegisterRequest로 name, username, password, phoneNumber, email, birthday를 받는다. <br>
	 *                생성일자(createdAt)는 현재 시간, point와 totalPaymentAmount는 0으로 초기화 <br>
	 *                권한은 MEMBER, 등급은 STANDARD, 상태는 ACTIVE로 초기화 <br>
	 * @return MemberRegisterResponse에 이름, 성별, 유저 아이디, 전화번호, 이메일, 생일을 담아서 반환
	 * @throws EmailAlreadyExistsException    이미 존재하는 이메일입니다.
	 * @throws UsernameAlreadyExistsException 이미 존재하는 아이디입니다.
	 */
	@Override
	@Transactional
	public MemberRegisterResponse registerMember(MemberRegisterRequest request) {
		if (customerRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}
		if (customerRepository.existsByPhoneNumber(request.phoneNumber())) {
			throw new PhoneNumberAlreadyExistsException("이미 존재하는 전화번호입니다.");
		}
		if (memberRepository.existsByUsername(request.username())) {
			throw new UsernameAlreadyExistsException("이미 존재하는 아이디입니다.");
		}

		Customer newCustomer = EntityMapper.toCustomerEntity(request);
		Customer savedCustomer = customerRepository.save(newCustomer);

		Member newMember = Member.builder()
			.customer(savedCustomer)
			.authority(MEMBER)
			.grade(standard())
			.status(StatusType.ACTIVE)
			.gender(request.gender())
			.username(request.username())
			.birthday(request.birthday())
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();

		Member savedMember = memberRepository.save(newMember);
		createCart(savedMember);

		// 멤버 등록되었다고 이벤트 생성. 이제 event listener들이 event 잡아서 로직 실행해줌.
		RegisteredEvent registeredEvent = new RegisteredEvent(savedMember);
		publisher.publishEvent(registeredEvent);

		return DtoMapper.toRegisterDto(savedCustomer, savedMember);
	}

	/**
	 * 회원탈퇴 <br>
	 * 아이디와 비밀번호로 회원 검증 후 회원을 휴면 상태로 전환 <br>
	 *
	 * @param memberId 요청 회원의 PK id
	 *                 member의 status를 WITHDRAWN으로, 탈퇴 일시인 withdrawnAt을 현재 시간으로 변경
	 * @throws MemberNotFoundException   존재하지 않는 회원입니다.
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 */
	@Override
	@Transactional
	public void withdrawMember(Long memberId) {
		Member foundMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		if (foundMember.getAuthority() == ADMIN) {
			throw new BadRequestException("관리자 등급은 탈퇴할 수 없습니다.");
		}

		foundMember.changeToWithdrawn();
	}

	/**
	 * 회원 정보 수정 <br>
	 * 변경 가능한 정보는 name, password
	 *
	 * @param memberId 회원 PK id
	 * @param request  MemberUpdateRequest로 수정하고 싶은 이름과 비밀번호를 받음
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 */
	@Override
	@Transactional
	public void updateMember(Long memberId, MemberPasswordUpdateRequest request) {

		Customer foundCustomer = customerRepository.findById(memberId)
			.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

		if (request.password().equals(foundCustomer.getPassword())) {
			throw new PasswordDuplicateException("기존 비밀번호와 다른 비밀번호를 입력해야 합니다.");
		}

		foundCustomer.changeCustomerPassword(request.password());
	}

	/**
	 * 입력받은 username으로 회원의 PK id, 비밀번호, 권한을 조회
	 *
	 * @param username "abc123" 형식의 유저 아이디
	 * @return 회원이 존재하면 PK id, username, 비밀번호, 권한을 MemberAuthInfoResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 PK id, username, 비밀번호, 권한이 모두 null인 MemberAuthInfoResponse를 반환
	 */
	@Override
	public MemberAuthInfoResponse getMemberAuthInfoByUsername(String username) {

		return memberRepository.findByUsername(username)
			.flatMap(foundMember -> customerRepository.findById(foundMember.getId())
				.map(foundCustomer -> DtoMapper.toAuthInfoDto(foundCustomer, foundMember))
			)
			.orElseGet(DtoMapper::toNullAuthInfoDto);
	}

	/**
	 * 입력받은 이메일로 회원의 PK id, username, 비밀번호, 권한을 조회
	 *
	 * @param email 유저 이메일
	 * @return 회원이 존재하면 PK id, username, 비밀번호, 권한을 MemberAuthInfoResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 PK id, username, 비밀번호, 권한이 모두 null인 MemberAuthInfoResponse를 반환
	 */
	@Override
	public MemberAuthInfoResponse getMemberAuthInfoByEmail(String email) {

		return customerRepository.findByEmail(email)
			.flatMap(foundCustomer -> memberRepository.findById(foundCustomer.getId())
				.map(foundMember -> DtoMapper.toAuthInfoDto(foundCustomer, foundMember))
			)
			.orElseGet(DtoMapper::toNullAuthInfoDto);
	}

	/**
	 * 회원 PK id로 상세 정보 조회
	 *
	 * @param memberId 요청 회원의 PK id
	 * @return MemberDetailsResponse에 회원의 정보를 담아서 반환
	 * @throws MemberNotFoundException   존재하지 않는 회원입니다.
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 */
	@Override
	public MemberDetailsResponse getMemberDetails(Long memberId) {

		Member foundMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		Customer foundCustomer = customerRepository.findById(memberId)
			.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

		return DtoMapper.toDetailsDto(foundCustomer, foundMember);
	}

	/**
	 * 관리자가 다양한 검색 조건을 이용하여 회원 목록 조회
	 */
	@Override
	public Page<MemberSearchResponse> searchMembersWithPaging(MemberSearchRequest request, Pageable pageable) {
		return memberRepository.searchMembersWithPaging(request, pageable);
	}

	/**
	 * 회원의 최근 로그인 시간을 업데이트
	 */
	@Override
	@Transactional
	public void updateMemberLatestLoginAt(String username) {

		Member foundMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		foundMember.updateLatestLoginAt();
	}

	/**
	 * 회원의 휴면 상태를 ACTIVE로 재활성화
	 */
	@Override
	@Transactional
	public void reactiveMember(String username) {

		Member foundMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		foundMember.reactiveMemberStatus();
	}

	/**
	 * STANDARD 등급을 가져오는 메서드
	 */
	private Grade standard() {
		return gradeRepository.findByName("STANDARD")
			.orElseThrow(() -> new GradeNotFoundException("STANDARD 등급이 존재하지 않습니다."));
	}

	private void createCart(Member savedMember) {
		Cart cart = new Cart(savedMember);
		cartRepository.save(cart);
	}
}