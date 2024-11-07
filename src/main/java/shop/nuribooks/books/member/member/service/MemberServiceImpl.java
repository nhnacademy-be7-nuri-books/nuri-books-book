package shop.nuribooks.books.member.member.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.exception.member.GradeNotFoundException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.DtoMapper;
import shop.nuribooks.books.member.member.dto.EntityMapper;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.UsernameAlreadyExistsException;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.entity.StatusType;
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

	/**
	 * 회원등록 <br>
	 * 고객(customer) 생성 후 customerRepository에 저장 <br>
	 * 그 후, 회원(member) 생성 후 memberRepository에 저장
	 * @throws EmailAlreadyExistsException 이미 존재하는 이메일입니다.
	 * @throws UsernameAlreadyExistsException 이미 존재하는 아이디입니다.
	 * @param request
	 * MemberRegisterRequest로 name, username, password, phoneNumber, email, birthday를 받는다. <br>
	 * 생성일자(createdAt)는 현재 시간, point와 totalPaymentAmount는 0으로 초기화 <br>
	 * 권한은 MEMBER, 등급은 STANDARD, 상태는 ACTIVE로 초기화 <br>
	 * @return MemberRegisterResponse에 이름, 성별, 유저 아이디, 전화번호, 이메일, 생일을 담아서 반환
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
			.authority(AuthorityType.MEMBER)
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

		return DtoMapper.toRegisterDto(savedCustomer, savedMember);
	}

	/**
	 * 회원탈퇴 <br>
	 * 아이디와 비밀번호로 회원 검증 후 회원을 휴면 상태로 전환 <br>
	 * @throws MemberNotFoundException 존재하지 않는 회원입니다.
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 * @param memberId 요청 회원의 PK id
	 * member의 status를 WITHDRAWN으로, 탈퇴 일시인 withdrawnAt을 현재 시간으로 변경
	 */
	@Override
	@Transactional
	public void withdrawMember(Long memberId) {
		Member foundMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		if (!customerRepository.existsById(memberId)) {
			throw new CustomerNotFoundException("존재하지 않는 고객입니다.");
		}

		foundMember.changeToWithdrawn();
	}

	/**
	 * 회원 정보 수정 <br>
	 * 변경 가능한 정보는 name, password, phoneNumber
	 * @throws MemberNotFoundException 존재하지 않는 회원입니다.
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 * @param userId  아이디
	 * @param request MemberUpdateRequest로 수정하고 싶은 이름과 비밀번호, 전화번호를 받음 <br>
	 * 로그인 상태의 사용자만이 회원 정보를 수정할 수 있기 때문에 <br>
	 * 입력받은 userId의 member가 반드시 존재하는 상황이다. <br>
	 * 그러므로 userId를 통해 customerRepository에서 해당 customer를 찾고 정보 수정을 진행 <br>
	 * 입력받은 각각의 이름, 비밀번호, 전화번호로 customer의 정보 수정
	 * @return MemberUpdateResponse에 변경한 이름과 전화번호 담아서 반환
	 */
	@Override
	@Transactional
	public void updateMember(Long memberId, MemberUpdateRequest request) {

		Customer foundCustomer = customerRepository.findById(memberId)
			.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

		foundCustomer.changeCustomerInformation(request.name(), request.password());
	}

	/**
	 * 입력받은 username으로 회원의 PK id, 비밀번호, 권한을 조회
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
	 * 입력받은 아이디와 비밀번호로 회원 상세 정보 조회
	 * @throws MemberNotFoundException 존재하지 않는 회원입니다.
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 * @param memberId 요청 회원의 PK id
	 * @return MemberDetailsResponse에 회원의 정보를 담아서 반환
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
	 * 매일 04:00시 정각에 마지막 로그인 날짜가 90일이 지난 회원들을 찾아, <br>
	 * 그 중에서 상태가 ACTIVE인 회원들을 INACTIVE로 변경
	 */
	@Transactional
	@Scheduled(cron = "0 0 4 * * ?")
	public void checkInactiveMembers() {
		LocalDateTime thresholdDate = LocalDateTime.now().minusDays(90);

		memberRepository.findAllByLatestLoginAtBefore(thresholdDate)
			.stream()
			.filter(foundMember -> foundMember.getStatus() == StatusType.ACTIVE)
			.forEach(foundMember -> {
				log.info("신규 휴면 회원이 발생하였습니다. - 회원 아이디: {}", foundMember.getUsername());

				foundMember.changeToInactive();
			});
	}

	/**
	 * 매일 04:30분에 탈퇴 날짜가 1년이 지난 회원들을 찾아, <br>
	 * userId와, status, withdrawnAt을 제외한 모든 필드들을 NULL 또는 공백 처리
	 */
	@Scheduled(cron = "0 30 4 * * ?")
	@Transactional
	public void removeWithdrawnMembers() {
		LocalDateTime thresholdDate = LocalDateTime.now().minusYears(1);

		memberRepository.findAllByWithdrawnAtBefore(thresholdDate)
			.forEach(foundMember -> {
				log.info("탈퇴 후 1년이 지나 SOFT DELETE의 대상이 되는 회원을 발견했습니다. - 회원 아이디: {}",
					foundMember.getUsername());

				Customer foundCustomer = customerRepository.findById(foundMember.getId())
					.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

				foundCustomer.changeToSoftDeleted();
				foundMember.changeToSoftDeleted();
			});
	}

	/**
	 * STANDARD 등급을 가져오는 메서드
	 */
	private Grade standard() {
		return gradeRepository.findByName("STANDARD")
			.orElseThrow(() -> new GradeNotFoundException("STANDARD 등급이 존재하지 않습니다."));
	}
}
