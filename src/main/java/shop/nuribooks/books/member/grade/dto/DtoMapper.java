package shop.nuribooks.books.member.grade.dto;

import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.entity.Grade;

/**
 * @author Jprotection
 */
public class DtoMapper {

	// 기본 생성자를 private으로 선언하여 외부에서 객체를 생성할 수 없게 함
	private DtoMapper() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

	/**
	 * Grade를 GradeDetailsResponse로 변환
	 */
	public static GradeDetailsResponse toDetailsDto(Grade grade) {
		return GradeDetailsResponse.builder()
			.id(grade.getId())
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement(grade.getRequirement())
			.build();
	}

	/**
	 * Grade를 GradeListResponse로 변환
	 */
	public static GradeListResponse toListDto(Grade grade) {
		return GradeListResponse.builder()
			.id(grade.getId())
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement(grade.getRequirement())
			.build();
	}
}
