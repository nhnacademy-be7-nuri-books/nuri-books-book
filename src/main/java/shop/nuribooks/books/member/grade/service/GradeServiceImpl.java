package shop.nuribooks.books.member.grade.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.exception.member.GradeAlreadyExistsException;
import shop.nuribooks.books.exception.member.GradeInUseException;
import shop.nuribooks.books.exception.member.GradeNotFoundException;
import shop.nuribooks.books.member.grade.dto.DtoMapper;
import shop.nuribooks.books.member.grade.dto.EntityMapper;
import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeRegisterResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeUpdateResponse;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
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
	 * @return 입력받은 등급명이 이미 존재하면 예외를 반환하고, <br>
	 * 등급 등록에 성공하면 입력 받았던 정보를 다시 GradeRegisterResponse에 담아서 반환
	 */
	@Transactional
	public GradeRegisterResponse registerGrade(GradeRegisterRequest request) {
		if (gradeRepository.existsByName(request.name())) {
			throw new GradeAlreadyExistsException("이미 존재하는 등급입니다.");
		}
		Grade savedGrade = gradeRepository.save(EntityMapper.toGradeEntity(request));

		return DtoMapper.toRegisterDto(savedGrade);
	}

	/**
	 * 등급 상세 조회
	 * @param name 상세 정보를 조회하고 싶은 등급명을 받는다.
	 * @return 입력받은 등급명이 존재하지 않으면 예외를 반환하고, <br>
	 * 존재하면 해당 등급의 정보를 GradeDetailsResponse에 담아서 반환
	 */
	public GradeDetailsResponse getGradeDetails(String name) {
		Grade foundGrade = getGrade(name);

		return DtoMapper.toDetailsDto(foundGrade);
	}

	/**
	 * 등급 수정
	 * @param name 수정하고 싶은 등급명을 받는다.
	 * @param request 수정하고 싶은 내용의 등급명, 포인트 적립률, 승급 조건 금액을 GradeUpdateRequest로 받는다.
	 * @return 입력받은 등급명이 존재하지 않으면 예외를 반환하고, <br>
	 * 존재하면 해당 등급의 정보를 입력받은 것으로 수정하고 그 내용을 GradeUpdateResponse에 담아서 반환
	 */
	@Transactional
	public GradeUpdateResponse updateGrade(String name, GradeUpdateRequest request) {
		Grade foundGrade = getGrade(name);

		foundGrade.changeGradeInformation(request.name(), request.pointRate(), request.requirement());

		return DtoMapper.toUpdateDto(foundGrade);
	}

	/**
	 * 등급 삭제
	 * @param name 삭제하고 싶은 등급명을 받는다.
	 * 입력받은 등급명이 존재하지 않으면 예외를 반환하고, <br>
	 * 존재하면 해당 등급을 삭제
	 */
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
	public List<GradeListResponse> getGradeList() {
		List<Grade> grades = gradeRepository.findAll();

		return grades.stream()
			.map(DtoMapper::toListDto)
			.toList();
	}

	/**
	 * 이름으로 등급을 찾는 메서드 추출
	 */
	private Grade getGrade(String name) {
		return gradeRepository.findByName(name)
			.orElseThrow(() -> new GradeNotFoundException("해당 이름의 등급이 존재하지 않습니다."));
	}

}
