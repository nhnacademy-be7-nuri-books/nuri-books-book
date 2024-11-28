package shop.nuribooks.books.member.grade.dto;

import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.entity.Grade;

/**
 * @author Jprotection
 */
public class EntityMapper {

	// 기본 생성자를 private으로 선언하여 외부에서 객체를 생성할 수 없게 함
	private EntityMapper() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

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
