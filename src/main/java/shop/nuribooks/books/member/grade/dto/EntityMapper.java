package shop.nuribooks.books.member.grade.dto;

import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.entity.Grade;

/**
 * @author Jprotection
 */
public class EntityMapper {

	/**
	 * GradeRegisterRequest를 Grade로 변환
	 */
	public static Grade toGradeEntity(GradeRegisterRequest request) {
		return Grade.builder()
			.name(request.name().toUpperCase())
			.pointRate(request.pointRate())
			.requirement(request.requirement())
			.build();
	}
}
