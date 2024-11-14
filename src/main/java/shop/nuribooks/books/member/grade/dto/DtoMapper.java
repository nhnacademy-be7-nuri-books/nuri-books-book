package shop.nuribooks.books.member.grade.dto;

import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeRegisterResponse;
import shop.nuribooks.books.member.grade.entity.Grade;

/**
 * @author Jprotection
 */
public class DtoMapper {

	/**
	 * Grade를 GradeRegisterResponse로 변환
	 */
	public static GradeRegisterResponse toRegisterDto(Grade grade) {
		return GradeRegisterResponse.builder()
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement(grade.getRequirement())
			.build();
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
