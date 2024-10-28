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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
	@Operation(summary = "신규 등급 등록", description = "신규 등급을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "신규 등급 등록 성공"),
		@ApiResponse(responseCode = "400", description = "신규 등급 등록 요청 데이터가 유효하지 않음"),
		@ApiResponse(responseCode = "409", description = "등록된 등급이 이미 존재함")
	})
	@PostMapping
	public ResponseEntity<GradeRegisterResponse> gradeRegister(@RequestBody @Valid GradeRegisterRequest request) {
		GradeRegisterResponse response = gradeService.registerGrade(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * 등급 상세 조회
	 */
	@Operation(summary = "등급 상세 조회", description = "등급명으로 등급을 상세 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등급 상세 조회 성공"),
		@ApiResponse(responseCode = "404", description = "등급이 존재하지 않음")
	})
	@GetMapping("/{name}")
	public ResponseEntity<GradeDetailsResponse> getGradeDetails(@PathVariable String name) {
		GradeDetailsResponse response = gradeService.getGradeDetails(name);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 등급 수정
	 */
	@Operation(summary = "등급 수정", description = "등급명으로 등급을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등급 수정 성공"),
		@ApiResponse(responseCode = "400", description = "등급 수정 요청 데이터가 유효하지 않음"),
		@ApiResponse(responseCode = "404", description = "등급이 존재하지 않음")
	})
	@PatchMapping("/{name}")
	public ResponseEntity<GradeUpdateResponse> gradeUpdate(
		@PathVariable String name, @RequestBody @Valid GradeUpdateRequest request) {
		GradeUpdateResponse response = gradeService.updateGrade(name, request);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 등급 삭제
	 */
	@Operation(summary = "등급 삭제", description = "등급명으로 등급을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등급 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "등급이 존재하지 않음"),
		@ApiResponse(responseCode = "409", description = "등급이 이미 회원에게 할당되었음")
	})
	@DeleteMapping("/{name}")
	public ResponseEntity<ResponseMessage> gradeDelete(@PathVariable String name) {
		gradeService.deleteGrade(name);

		return ResponseEntity.status(OK).body(
			new ResponseMessage(OK.value(), "등급이 성공적으로 삭제되었습니다."));
	}

	/**
	 * 등급 목록 조회
	 */
	@Operation(summary = "등급 목록 조회", description = "전체 등급의 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등급 목록 조회 성공")
	})
	@GetMapping("/grades")
	public ResponseEntity<List<GradeListResponse>> getGradeList() {
		List<GradeListResponse> response = gradeService.getGradeList();

		return ResponseEntity.status(OK).body(response);
	}
}
