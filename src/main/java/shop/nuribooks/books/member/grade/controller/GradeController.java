package shop.nuribooks.books.member.grade.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeRegisterResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeUpdateResponse;
import shop.nuribooks.books.member.grade.service.GradeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/grade")
public class GradeController {

	private final GradeService gradeService;

	/**
	 * 등급 등록
	 */
	@PostMapping
	public ResponseEntity<GradeRegisterResponse> gradeRegister(@RequestBody GradeRegisterRequest request) {
		GradeRegisterResponse response = gradeService.registerGrade(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * 등급 상세 조회
	 */
	@GetMapping("/{name}")
	public ResponseEntity<GradeDetailsResponse> getGradeDetails(@PathVariable String name) {
		GradeDetailsResponse response = gradeService.getGradeDetails(name);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 등급 수정
	 */
	@PatchMapping("/{name}")
	public ResponseEntity<GradeUpdateResponse> gradeUpdate(
		@PathVariable String name, @RequestBody GradeUpdateRequest request) {
		GradeUpdateResponse response = gradeService.updateGrade(name, request);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 등급 삭제
	 */
	@DeleteMapping("/{name}")
	public ResponseEntity<ResponseMessage> gradeDelete(@PathVariable String name) {
		gradeService.deleteGrade(name);

		return ResponseEntity.status(OK).body(
			new ResponseMessage(OK.value(), "등급이 성공적으로 삭제되었습니다."));
	}

	/**
	 * 등급 목록 조회
	 */
	@GetMapping("/grades")
	public ResponseEntity<List<GradeListResponse>> getGradeList() {
		List<GradeListResponse> response = gradeService.getGradeList();

		return ResponseEntity.status(OK).body(response);
	}
}
