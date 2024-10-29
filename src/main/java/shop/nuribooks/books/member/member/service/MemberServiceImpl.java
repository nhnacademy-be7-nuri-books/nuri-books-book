package shop.nuribooks.books.member.member.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.exception.member.GradeNotFoundException;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.DtoMapper;
import shop.nuribooks.books.member.member.dto.EntityMapper;
import shop.nuribooks.books.member.member.dto.request.MemberDetailsRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberWithdrawRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.InvalidPasswordException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.UserIdAlreadyExistsException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.member.resignedmember.repository.ResignedMemberRepository;

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
	private final ResignedMemberRepository resignedMemberRepository;
	private final AddressRepository addressRepository;

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
	 * @return MemberRegisterResponse에 이름, 성별, 유저 아이디, 전화번호, 이메일, 생일을 담아서 반환
	 */
	@Transactional
	public MemberRegisterResponse registerMember(MemberRegisterRequest request) {
		if (customerRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}
		if (memberRepository.existsByUserId(request.userId())) {
			throw new UserIdAlreadyExistsException("이미 존재하는 아이디입니다.");
		}

		Customer newCustomer = EntityMapper.toCustomerEntity(request);
		Customer savedCustomer = customerRepository.save(newCustomer);

		Member newMember = Member.builder()
			.customer(savedCustomer)
			.authority(AuthorityType.MEMBER)
			.grade(standard())
			.status(StatusType.ACTIVE)
			.gender(request.gender())
			.userId(request.userId())
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
		Member foundMember = memberRepository.findByUserId(request.userId())
			.orElseThrow(() -> new UserIdNotFoundException("존재하지 않는 아이디입니다."));

		if (!customerRepository.existsByIdAndPassword(
			foundMember.getId(), request.password())) {
			throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
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
	@Transactional
	public MemberUpdateResponse updateMember(String userId, MemberUpdateRequest request) {
		Member foundMember = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		Customer findCustomer = customerRepository.findById(foundMember.getId())
			.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

		findCustomer.changeCustomerInformation(
			request.name(), request.password(), request.phoneNumber());

		return DtoMapper.toUpdateDto(findCustomer);
	}

	/**
	 * 입력받은 아이디로 회원의 이름, 비밀번호, 권한을 조회
	 * @param userId 아이디
	 * @return 회원이 존재하면 이름, 비밀번호, 권한을 MemberAuthInfoResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 이름, 비밀번호, 권한이 모두 null인 MemberAuthInfoResponse를 반환
	 */
	public MemberAuthInfoResponse getMemberAuthInfo(String userId) {

		return memberRepository.findByUserId(userId)
			.flatMap(foundMember -> customerRepository.findById(foundMember.getId())
				.map(foundCustomer -> DtoMapper.toAuthInfoDto(foundCustomer, foundMember))
			)
			.orElseGet(DtoMapper::toNullDto);
	}

	/**
	 * 입력받은 아이디와 비밀번호로 회원 상세 정보 조회
	 * @throws MemberNotFoundException 존재하지 않는 회원입니다.
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 * @param request MemberDetailsRequest로 상세 조회하고 싶은 userId와 password를 받음
	 * @return MemberDetailsResponse에 회원의 모든 정보를 담아서 반환
	 */
	public MemberDetailsResponse getMemberDetails(MemberDetailsRequest request) {

		Member foundMember = memberRepository.findByUserId(request.userId())
			.orElseThrow(() -> new UserIdNotFoundException("존재하지 않는 아이디입니다."));

		Customer foundCustomer = customerRepository.findByIdAndPassword(foundMember.getId(), request.password())
			.orElseThrow(() -> new InvalidPasswordException("비밀번호가 일치하지 않습니다."));

		return DtoMapper.toDetailsDto(foundCustomer, foundMember);
	}

	// /**
	//  * @deprecated 이 스케쥴링 메서드는 사용되지 않을 것입니다.
	//  * 탈퇴 회원의 Withdrawn상태 기간 경과를 매일 자정에 확인하여 1년이 지나면 <br>
	//  * 해당 회원들의 주소를 모두 찾아 AddressRepository에서 삭제하고, <br>
	//  * MemberRepository에서 해당 회원을, CustomerRepository에서도 동일한 id의 비회원을 삭제한다. <br>
	//  * 그 회원이 사용했던 userId만을 ResignedMember에 담아 ResignedMemberRepository에 저장
	//  */
	// @Deprecated
	// @Scheduled(cron = "0 0 0 * * ?")
	// @Transactional
	// public void removeWithdrawnMembers() {
	// 	try {
	// 		List<Member> membersToDelete = memberRepository.findAll().stream()
	// 			.filter(Member::isWithdrawnForOverOneYear)
	// 			.toList();
	//
	// 		if (membersToDelete.isEmpty()) {
	// 			log.info("탈퇴 일시가 1년이 지난 회원이 존재하지 않습니다.");
	// 			return;
	// 		}
	//
	// 		List<Customer> customersToDelete = membersToDelete.stream()
	// 			.map(member -> customerRepository.findById(member.getId())
	// 				.orElseThrow(() -> new CustomerNotFoundException("삭제하려는 고객이 존재하지 않습니다.")))
	// 			.toList();
	//
	// 		List<Address> addressesToDelete = membersToDelete.stream()
	// 			.flatMap(member -> addressRepository.findAllByMemberId(member.getId()).stream())
	// 			.toList();
	//
	// 		List<ResignedMember> completelyResignedMembers = membersToDelete.stream()
	// 			.map(member -> ResignedMember.builder()
	// 				.resignedUserId(member.getUserId())
	// 				.build())
	// 			.toList();
	//
	// 		addressRepository.deleteAll(addressesToDelete);
	// 		memberRepository.deleteAll(membersToDelete);
	// 		customerRepository.deleteAll(customersToDelete);
	// 		resignedMemberRepository.saveAll(completelyResignedMembers);
	//
	// 	} catch (Exception e) {
	// 		log.error("회원의 탈퇴 일시 경과를 확인하는 스케줄링에 에러가 발생했습니다 : {}", e.getMessage());
	// 	}
	// }

	/**
	 * STANDARD 등급을 가져오는 메서드
	 */
	private Grade standard() {
		return gradeRepository.findByName("STANDARD")
			.orElseThrow(() -> new GradeNotFoundException("STANDARD 등급이 존재하지 않습니다."));
	}
}
