package shop.nuribooks.books.member.grade.service;

import java.util.List;

import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;

/**
 * @author Jprotection
 */
public interface GradeService {

	void registerGrade(GradeRegisterRequest request);

	GradeDetailsResponse getGradeDetails(String name);

	void updateGrade(String name, GradeUpdateRequest request);

	void deleteGrade(String name);

	List<GradeListResponse> getGradeList();
}
