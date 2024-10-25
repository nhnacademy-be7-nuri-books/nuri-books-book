package shop.nuribooks.books.member.grade.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.member.grade.dto.request.GradeUpdateRequest;
import shop.nuribooks.books.member.grade.dto.response.GradeUpdateResponse;
import shop.nuribooks.books.member.grade.service.GradeService;

@WebMvcTest(GradeController.class)
public class GradeControllerTest {

	@MockBean
	private GradeService gradeService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("등급 수정")
	@Test
	public void gradeUpdate() throws Exception{
	    //given
		GradeUpdateRequest request = gradeUpdateRequest();
		GradeUpdateResponse response = gradeUpdateResponse();
		String requestName = "GOLD";

		when(gradeService.updateGrade(eq(requestName), any(GradeUpdateRequest.class)))
			.thenReturn(response);

	    //when
		ResultActions result = mockMvc.perform(patch("/api/member/grade/{name}", requestName)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("pointRate").value(response.pointRate()))
			.andExpect(jsonPath("requirement").value(response.requirement()));
	}

	private GradeUpdateRequest gradeUpdateRequest() {
		return GradeUpdateRequest.builder()
			.name("GOLD")
			.pointRate(6)
			.requirement(BigDecimal.valueOf(300_000))
			.build();
	}

	private GradeUpdateResponse gradeUpdateResponse() {
		return GradeUpdateResponse.builder()
			.name("GOLD")
			.pointRate(6)
			.requirement(BigDecimal.valueOf(300_000))
			.build();
	}

}
