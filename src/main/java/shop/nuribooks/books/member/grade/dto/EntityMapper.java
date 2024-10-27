package shop.nuribooks.books.member.grade.dto;

import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.entity.Grade;

public class EntityMapper {

	public static Grade toGradeEntity(GradeRegisterRequest request) {
		return Grade.builder()
			.name(request.name())
			.pointRate(request.pointRate())
			.requirement(request.requirement())
			.build();
	}
}
