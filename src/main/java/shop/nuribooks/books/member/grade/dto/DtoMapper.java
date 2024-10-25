package shop.nuribooks.books.member.grade.dto;

import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeRegisterResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeUpdateResponse;
import shop.nuribooks.books.member.grade.entity.Grade;

public class DtoMapper {

	public static GradeRegisterResponse toRegisterDto(Grade grade) {
		return GradeRegisterResponse.builder()
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement(grade.getRequirement())
			.build();
	}

	public static GradeDetailsResponse toDetailsDto(Grade grade) {
		return GradeDetailsResponse.builder()
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement(grade.getRequirement())
			.build();
	}

	public static GradeUpdateResponse toUpdateDto(Grade grade) {
		return GradeUpdateResponse.builder()
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement((grade.getRequirement()))
			.build();
	}

	public static GradeListResponse toListDto(Grade grade) {
		return GradeListResponse.builder()
			.name(grade.getName())
			.pointRate(grade.getPointRate())
			.requirement(grade.getRequirement())
			.build();
	}
}
