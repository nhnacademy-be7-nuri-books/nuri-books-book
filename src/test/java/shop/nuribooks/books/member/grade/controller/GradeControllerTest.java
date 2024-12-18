package shop.nuribooks.books.member.grade.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.member.grade.dto.DtoMapper;
import shop.nuribooks.books.member.grade.dto.MemberGradeBatchDto;
import shop.nuribooks.books.member.grade.dto.request.GradeRegisterRequest;
import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeDetailsResponse;
import shop.nuribooks.books.member.grade.dto.response.GradeListResponse;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.service.GradeService;

@WebMvcTest(GradeController.class)
class GradeControllerTest {

	@MockBean
	protected GradeService gradeService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("등급 등록 성공")
	@Test
	void gradeRegister() throws Exception {
		//given
		GradeRegisterRequest request = getGradeRegisterRequest();

		doNothing().when(gradeService).registerGrade(any(GradeRegisterRequest.class));

		//when
		ResultActions result = mockMvc.perform(post("/api/members/grades")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value("201"))
			.andExpect(jsonPath("message").value("등급이 성공적으로 생성되었습니다."));
	}

	@DisplayName("등급 등록 실패 - validation 에러")
	@Test
	void gradeRegister_invalidRequest() throws Exception {
		//given
		GradeRegisterRequest badRequest = getBadGradeRegisterRequest();

		//when
		ResultActions badResult = mockMvc.perform(post("/api/members/grades")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("등급명은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("포인트 적립률은 0 이상의 정수입니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("등급의 승급 조건 금액은 1억원을 초과할 수 없습니다.")));
	}

	@DisplayName("등급명으로 등급 상세 조회 성공")
	@Test
	void getGradeDetails() throws Exception {
		//given
		String requiredName = "STANDARD";
		GradeDetailsResponse response = getGradeDetailsResponse();

		when(gradeService.getGradeDetails(requiredName)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/members/grades/{name}", requiredName));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(response.id()))
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("pointRate").value(response.pointRate()))
			.andExpect(jsonPath("requirement").value(response.requirement()));
	}

	@DisplayName("등급명으로 등급 수정 성공")
	@Test
	void gradeUpdate() throws Exception {
		//given
		GradeUpdateRequest request = getGradeUpdateRequest();
		String requestName = "MASTER";

		doNothing().when(gradeService).updateGrade(eq(requestName), any(GradeUpdateRequest.class));

		//when
		ResultActions result = mockMvc.perform(put("/api/members/grades/{name}", requestName)
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message").value("등급이 성공적으로 수정되었습니다."));
	}

	@DisplayName("등급명으로 등급 수정 - validation 에러")
	@Test
	void gradeUpdate_invalidRequest() throws Exception {
		//given
		GradeUpdateRequest badRequest = getBadGradeUpdateRequest();
		String requiredName = "STANDARD";

		//when
		ResultActions badResult = mockMvc.perform(put("/api/members/grades/{name}", requiredName)
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("등급명은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("포인트 적립률은 100을 초과할 수 없습니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("등급의 승급 조건 금액은 0원 이상이어야 합니다.")));
	}

	@DisplayName("등급명으로 등급 삭제 성공")
	@Test
	void gradeDelete() throws Exception {
		//given
		String requiredName = "STANDARD";

		doNothing().when(gradeService).deleteGrade(requiredName);

		//when
		ResultActions result = mockMvc.perform(delete("/api/members/grades/{name}", requiredName));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message").value("등급이 성공적으로 삭제되었습니다."));
	}

	@DisplayName("등급 목록 조회")
	@Test
	void getGradeList() throws Exception {
		//given
		List<GradeListResponse> response = getGradeListResponse();

		when(gradeService.getGradeList()).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/members/grades"));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(2)))
			.andExpect(jsonPath("$[0].name").value(response.get(0).name()))
			.andExpect(jsonPath("$[1].name").value(response.get(1).name()));
	}

	@DisplayName("등급과 회원 배치 리스트 전송 성공")
	@Test
	void getMemberGradeBatchListByRequirement() throws Exception {
		//given
		List<MemberGradeBatchDto> response = getMemberGradeBatchDtoList();
		when(gradeService.getMemberGradeBatchListByRequirement()).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/members/grades/batch-list"));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(2)))
			.andExpect(jsonPath("$[0].customerId").value(response.get(0).customerId()))
			.andExpect(jsonPath("$[1].gradeId").value(response.get(1).gradeId()));
	}

	/**
	 * 테스트를 위한 GradeRegisterRequest 생성
	 */
	private GradeRegisterRequest getGradeRegisterRequest() {
		return GradeRegisterRequest.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	/**
	 * 테스트를 위한 잘못된 GradeRegisterRequest 생성
	 */
	private GradeRegisterRequest getBadGradeRegisterRequest() {
		return GradeRegisterRequest.builder()
			.name("  ")
			.pointRate(-1)
			.requirement(BigDecimal.valueOf(100_000_001))
			.build();
	}

	/**
	 * 테스트를 위한 GradeDetailsResponse 생성
	 */
	private GradeDetailsResponse getGradeDetailsResponse() {
		return GradeDetailsResponse.builder()
			.id(1)
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	/**
	 * 테스트를 위한 GradeUpdateRequest 생성
	 */
	private GradeUpdateRequest getGradeUpdateRequest() {
		return GradeUpdateRequest.builder()
			.name("GOLD")
			.pointRate(6)
			.requirement(BigDecimal.valueOf(300_000))
			.build();
	}

	/**
	 * 테스트를 위한 잘못된 GradeUpdateRequest 생성
	 */
	private GradeUpdateRequest getBadGradeUpdateRequest() {
		return GradeUpdateRequest.builder()
			.name("  ")
			.pointRate(200)
			.requirement(BigDecimal.valueOf(-100_000))
			.build();
	}

	/**
	 * 테스트를 위한 등급 목록 생성
	 */
	private List<Grade> getSavedGrades() {
		return Arrays.asList(
			new Grade(1, "STANDARD", 3, BigDecimal.valueOf(100_000)),
			new Grade(2, "SILVER", 5, BigDecimal.valueOf(200_000))
		);
	}

	/**
	 * 테스트를 위한 GradeListResponse 생성
	 */
	private List<GradeListResponse> getGradeListResponse() {
		return getSavedGrades().stream()
			.map(DtoMapper::toListDto)
			.collect(Collectors.toList());
	}

	/**
	 * 테스트를 위한 MemberGradeBatchDto 리스트 생성
	 */
	private List<MemberGradeBatchDto> getMemberGradeBatchDtoList() {
		return List.of(
			new MemberGradeBatchDto(1L, 1), new MemberGradeBatchDto(2L, 2));
	}
}
