package shop.nuribooks.books.member.grade.service;

import java.util.List;

import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeRegisterResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeUpdateResponse;

/**
 * @author Jprotection
 */
public interface GradeService {

	GradeRegisterResponse registerGrade(GradeRegisterRequest request);

	GradeDetailsResponse getGradeDetails(String name);

	GradeUpdateResponse updateGrade(String name, GradeUpdateRequest request);

	void deleteGrade(String name);

	List<GradeListResponse> getGradeList();
}
