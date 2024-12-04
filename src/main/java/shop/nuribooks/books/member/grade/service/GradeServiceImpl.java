package shop.nuribooks.books.member.grade.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.member.GradeAlreadyExistsException;
import shop.nuribooks.books.exception.member.GradeInUseException;
import shop.nuribooks.books.exception.member.GradeNotFoundException;
import shop.nuribooks.books.member.grade.dto.DtoMapper;
import shop.nuribooks.books.member.grade.dto.EntityMapper;
import shop.nuribooks.books.member.grade.dto.MemberGradeBatchDto;
import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

/**
 * @author Jprotection
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {

	private final GradeRepository gradeRepository;
	private final MemberRepository memberRepository;

	/**
	 * 등급 등록
	 * @param request GradeRegisterRequest로 등급의 이름, 포인트 적립률, 승급 조건 금액을 받는다.
	 * 등급 등록에 성공하면 입력 받았던 정보를 다시 GradeRegisterResponse에 담아서 반환
	 */
	@Override
	@Transactional
	public void registerGrade(GradeRegisterRequest request) {
		if (gradeRepository.existsByName(request.name().toUpperCase())) {
			throw new GradeAlreadyExistsException("이미 존재하는 등급입니다.");
		}

		gradeRepository.save(EntityMapper.toGradeEntity(request));
	}

	/**
	 * 등급 상세 조회
	 * @param name 상세 정보를 조회하고 싶은 등급명을 받는다.
	 * @return 입력받은 등급명이 존재하지 않으면 예외를 반환하고, <br>
	 * 존재하면 해당 등급의 정보를 GradeDetailsResponse에 담아서 반환
	 */
	@Override
	public GradeDetailsResponse getGradeDetails(String name) {
		Grade foundGrade = getGrade(name);

		return DtoMapper.toDetailsDto(foundGrade);
	}

	/**
	 * 등급 수정
	 * @param name 수정하고 싶은 등급명을 받는다.
	 * @param request 수정하고 싶은 내용의 등급명, 포인트 적립률, 승급 조건 금액을 GradeUpdateRequest로 받는다.
	 * 해당 등급의 정보를 입력받은 것으로 수정하고 그 내용을 GradeUpdateResponse에 담아서 반환
	 */
	@Override
	@Transactional
	public void updateGrade(String name, GradeUpdateRequest request) {
		Grade foundGrade = getGrade(name);

		foundGrade.changeGradeInformation(request.name().toUpperCase(), request.pointRate(), request.requirement());
	}

	/**
	 * 등급 삭제
	 * @param name 삭제하고 싶은 등급명을 받는다.
	 * 입력받은 등급명이 존재하지 않으면 예외를 반환하고, <br>
	 * 존재하면 해당 등급을 삭제
	 */
	@Override
	@Transactional
	public void deleteGrade(String name) {
		Grade foundGrade = getGrade(name);
		if (memberRepository.existsByGradeId(foundGrade.getId())) {
			throw new GradeInUseException("해당 등급을 가진 회원이 존재하여 삭제할 수 없습니다.");
		}
		gradeRepository.delete(foundGrade);
	}

	/**
	 * 등급 목록 조회
	 * @return 각 등급의 정보를 GradeListResponse에 담아서 List로 반환
	 */
	@Override
	public List<GradeListResponse> getGradeList() {
		List<Grade> grades = gradeRepository.findAll().stream()
			.sorted(Comparator.comparing(Grade::getRequirement))
			.toList();

		return IntStream.range(0, grades.size())  // 순차적인 인덱스를 생성
			.mapToObj(index -> {
				Grade grade = grades.get(index);
				// 새로운 GradeListResponse 객체를 id를 포함하여 생성
				return GradeListResponse.builder()
					.id(index + 1)  // 순차적인 id (1부터 시작)
					.name(grade.getName())
					.pointRate(grade.getPointRate())
					.requirement(grade.getRequirement())
					.build();
			})
			.toList();
	}

	/**
	 * 배치 서버로 각 등급의 requirement를 만족하는 member의 id와 해당 등급의 id를 반환
	 */
	@Override
	public List<MemberGradeBatchDto> getMemberGradeBatchListByRequirement() {
		List<MemberGradeBatchDto> batchList = new ArrayList<>();

		List<Grade> gradeList = gradeRepository.findAll().stream()
			.sorted(Comparator.comparing(Grade::getRequirement))
			.toList();

		// 각 member의 소비 금액에 대해 적합한 grade의 requirement 구간 찾기
		for (Member member : memberRepository.findAll()) {
			BigDecimal payment = member.getTotalPaymentAmount();

			// 각 grade의 requirement에 맞는 범위 찾기
			for (int i = 0; i < gradeList.size(); i++) {
				BigDecimal lowerRequirement =
					(i == 0) ? BigDecimal.ZERO : gradeList.get(i).getRequirement();
				BigDecimal upperRequirement =
					(i == gradeList.size() - 1) ? null : gradeList.get(i + 1).getRequirement();

				if (upperRequirement == null) {
					batchList.add(new MemberGradeBatchDto(member.getId(), gradeList.get(i).getId()));
					break;
				}

				// member의 소비 금액이 (i)번째 requirement와 (i+1)번째 requirement 사이에 해당하는지 확인
				if ((lowerRequirement.compareTo(payment) <= 0) && (payment.compareTo(upperRequirement) < 0)) {
					batchList.add(new MemberGradeBatchDto(member.getId(), gradeList.get(i).getId()));
					break; // 적합한 범위를 찾으면 더 이상 확인할 필요 없음
				}
			}
		}

		return batchList;
	}

	/**
	 * 이름으로 등급을 찾는 메서드 추출
	 */
	private Grade getGrade(String name) {

		if (Objects.isNull(name)) {
			throw new BadRequestException("등급명을 입력해주세요.");
		}

		return gradeRepository.findByName(name.toUpperCase())
			.orElseThrow(() -> new GradeNotFoundException("해당 이름의 등급이 존재하지 않습니다."));
	}
}
