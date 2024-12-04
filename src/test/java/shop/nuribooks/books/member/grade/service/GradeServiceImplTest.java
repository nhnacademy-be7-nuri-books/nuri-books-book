package shop.nuribooks.books.member.grade.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.member.GradeAlreadyExistsException;
import shop.nuribooks.books.exception.member.GradeInUseException;
import shop.nuribooks.books.exception.member.GradeNotFoundException;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.dto.MemberGradeBatchDto;
import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

	@InjectMocks
	private GradeServiceImpl gradeServiceImpl;

	@Mock
	private GradeRepository gradeRepository;

	@Mock
	private MemberRepository memberRepository;

	@DisplayName("등급 등록 성공")
	@Test
	void registerGrade() {
		//given
		GradeRegisterRequest request = getGradeRegisterRequest();
		Grade savedGrade = getSavedGrade();
		when(gradeRepository.existsByName(request.name())).thenReturn(false);
		when(gradeRepository.save(any(Grade.class))).thenReturn(savedGrade);

		//when
		gradeServiceImpl.registerGrade(request);

		//then
		verify(gradeRepository, times(1)).existsByName(request.name());
		verify(gradeRepository, times(1)).save(any(Grade.class));
	}

	@DisplayName("등급 등록 실패 - 이미 존재하는 등급")
	@Test
	void registerGrade_gradeAlreadyExists() {
		//given
		GradeRegisterRequest request = getGradeRegisterRequest();
		when(gradeRepository.existsByName(request.name())).thenReturn(true);

		//when / then
		assertThatThrownBy(() -> gradeServiceImpl.registerGrade(request))
			.isInstanceOf(GradeAlreadyExistsException.class)
			.hasMessage("이미 존재하는 등급입니다.");
	}

	@DisplayName("등급명으로 등급 상세 조회 성공")
	@Test
	void getGradeDetails() {
		//given
		Grade savedGrade = getSavedGrade();
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.of(savedGrade));

		//when
		GradeDetailsResponse response = gradeServiceImpl.getGradeDetails(requiredName);

		//then
		assertThat(response.id()).isEqualTo(savedGrade.getId());
		assertThat(response.name()).isEqualTo(savedGrade.getName());
		assertThat(response.pointRate()).isEqualTo(savedGrade.getPointRate());
		assertThat(response.requirement()).isEqualTo(savedGrade.getRequirement());
	}

	@DisplayName("등급명으로 등급 상세 조회 실패 - 존재하지 않는 등급")
	@Test
	void getGradeDetails_gradeNotFound() {
		//given
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.empty());

		//when / then
		assertThatThrownBy(() -> gradeServiceImpl.getGradeDetails(requiredName))
			.isInstanceOf(GradeNotFoundException.class)
			.hasMessage("해당 이름의 등급이 존재하지 않습니다.");
	}

	@DisplayName("등급명으로 등급 상세 조회 실패 - 존재하지 않는 등급")
	@Test
	void getGradeDetails_nameIsNull() {
		//given
		String requiredName = null;

		//when / then
		assertThatThrownBy(() -> gradeServiceImpl.getGradeDetails(requiredName))
			.isInstanceOf(BadRequestException.class);
	}

	@DisplayName("등급명으로 등급 수정 성공")
	@Test
	void updateGrade() {
		//given
		Grade savedGrade = spy(getSavedGrade());
		GradeUpdateRequest request = getGradeUpdateRequest();
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.of(savedGrade));

		//when
		gradeServiceImpl.updateGrade(requiredName, request);

		//then
		verify(savedGrade, times(1))
			.changeGradeInformation(request.name(), request.pointRate(), request.requirement());
	}

	@DisplayName("등급명으로 등급 수정 실패 - 존재하지 않는 등급")
	@Test
	void updateGrade_gradeNotFound() {
		//given
		GradeUpdateRequest request = getGradeUpdateRequest();
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.empty());

		//when /then
		assertThatThrownBy(() -> gradeServiceImpl.updateGrade(requiredName, request))
			.isInstanceOf(GradeNotFoundException.class)
			.hasMessage("해당 이름의 등급이 존재하지 않습니다.");
	}

	@DisplayName("등급명으로 등급 삭제 성공")
	@Test
	void deleteGrade() {
		//given
		Grade savedGrade = getSavedGrade();
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.of(savedGrade));
		when(memberRepository.existsByGradeId(savedGrade.getId())).thenReturn(false);
		doNothing().when(gradeRepository).delete(savedGrade);

		//when
		gradeServiceImpl.deleteGrade(requiredName);

		//then
		verify(gradeRepository, times(1)).findByName(requiredName);
		verify(gradeRepository, times(1)).delete(savedGrade);
	}

	@DisplayName("등급명으로 등급 삭제 실패 - 존재하지 않는 등급")
	@Test
	void deleteGrade_gradeNotFound() {
		//given
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.empty());

		//when / then
		assertThatThrownBy(() -> gradeServiceImpl.deleteGrade(requiredName))
			.isInstanceOf(GradeNotFoundException.class)
			.hasMessage("해당 이름의 등급이 존재하지 않습니다.");
	}

	@DisplayName("등급명으로 등급 삭제 실패 - 해당 등급이 이미 회원에게 할당됨")
	@Test
	void deleteGrade_gradeInUse() {
		//given
		Grade savedGrade = getSavedGrade();
		String requiredName = "STANDARD";

		when(gradeRepository.findByName(requiredName)).thenReturn(Optional.of(savedGrade));
		when(memberRepository.existsByGradeId(savedGrade.getId())).thenReturn(true);

		//when / then
		assertThatThrownBy(() -> gradeServiceImpl.deleteGrade(requiredName))
			.isInstanceOf(GradeInUseException.class)
			.hasMessage("해당 등급을 가진 회원이 존재하여 삭제할 수 없습니다.");
	}

	@DisplayName("전체 등급 목록 조회")
	@Test
	void getGradeList() {
		//given
		List<Grade> savedGrades = getSavedGrades();
		when(gradeRepository.findAll()).thenReturn(savedGrades);

		//when
		List<GradeListResponse> response = gradeServiceImpl.getGradeList();

		//then
		assertThat(response).hasSize(2);
		assertThat(response.get(0).name()).isEqualTo(savedGrades.get(0).getName());
		assertThat(response.get(1).name()).isEqualTo(savedGrades.get(1).getName());
	}

	@DisplayName("회원과 등급 배치 리스트 생성")
	@Test
	void getMemberGradeBatchListByRequirement() {
		//given
		List<Grade> savedGrades = getSavedGrades();
		List<Member> savedMembers = List.of(getSavedMember());

		when(gradeRepository.findAll()).thenReturn(savedGrades);
		when(memberRepository.findAll()).thenReturn(savedMembers);

		//when
		List<MemberGradeBatchDto> response = gradeServiceImpl.getMemberGradeBatchListByRequirement();

		//then
		assertThat(response).hasSize(1);
		assertThat(response.get(0).customerId()).isEqualTo(savedMembers.get(0).getCustomer().getId());
		assertThat(response.get(0).gradeId()).isEqualTo(savedGrades.get(1).getId());
	}

	/**
	 * 테스트를 위한 GradeRegisterRequest 생성
	 */
	private GradeRegisterRequest getGradeRegisterRequest() {
		return GradeRegisterRequest.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	/**
	 * 테스트를 위한 GradeUpdateRequest 생성
	 */
	private GradeUpdateRequest getGradeUpdateRequest() {
		return GradeUpdateRequest.builder()
			.name("수정하고 싶은 등급명")
			.pointRate(10)
			.requirement(BigDecimal.valueOf(500_000))
			.build();
	}

	/**
	 * 테스트를 위한 등급 생성
	 */
	private Grade getSavedGrade() {
		return Grade.builder()
			.id(1)
			.name("STANDARD")
			.pointRate(1)
			.requirement(BigDecimal.valueOf(0))
			.build();
	}

	/**
	 * 테스트를 위한 등급 목록 생성
	 */
	private List<Grade> getSavedGrades() {
		return Arrays.asList(
			new Grade(1, "STANDARD", 1, BigDecimal.valueOf(0)),
			new Grade(2, "GOLD", 3, BigDecimal.valueOf(200_000))
		);
	}

	/**
	 * 테스트를 위한 회원 목록 생성
	 */
	private Member getSavedMember() {
		return Member.builder()
			.id(1L)
			.customer(getSavedCustomer())
			.authority(AuthorityType.MEMBER)
			.grade(getGrade())
			.status(StatusType.ACTIVE)
			.gender(GenderType.MALE)
			.username("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.valueOf(200000))
			.build();
	}

	/**
	 * 테스트를 위한 등급 생성
	 */
	private Grade getGrade() {
		return Grade.builder()
			.id(1)
			.name("STANDARD")
			.pointRate(1)
			.requirement(BigDecimal.valueOf(0))
			.build();
	}

	/**
	 * 테스트를 위한 비회원 생성
	 */
	private Customer getSavedCustomer() {
		return Customer.builder()
			.id(1L)
			.name("boho")
			.password("abc123")
			.phoneNumber("01082828282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}
}
