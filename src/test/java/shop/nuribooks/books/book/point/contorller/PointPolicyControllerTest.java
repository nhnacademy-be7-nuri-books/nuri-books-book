package shop.nuribooks.books.book.point.contorller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.point.controller.PointPolicyController;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.service.PointPolicyService;
import shop.nuribooks.books.common.ControllerTestSupport;

public class PointPolicyControllerTest extends ControllerTestSupport {
	@Autowired
	private MockMvc mockMvc;


	@Autowired
	private ObjectMapper objectMapper;

	private PointPolicyRequest pointPolicyRequest;
	private PointPolicyResponse pointPolicyResponse;

	@BeforeEach
	void setUp() {
		pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "이름", BigDecimal.valueOf(10));
		pointPolicyResponse = new PointPolicyResponse() {
			@Override
			public long getId() {
				return 1;
			}

			@Override
			public PolicyType getPolicyType() {
				return PolicyType.FIXED;
			}

			@Override
			public String getName() {
				return "이름";
			}

			@Override
			public BigDecimal getAmount() {
				return BigDecimal.valueOf(10);
			}
		};
	}

	@Test
	void getTest() throws Exception {
		when(pointPolicyService.getPointPolicyResponses(any())).thenReturn(
			new PageImpl(List.of(this.pointPolicyResponse),
				PageRequest.of(0, 10), 1));
		mockMvc.perform(get("/api/point-policies"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@Test
	void registeredByAppJsonTest() throws Exception {
		when(pointPolicyService.registerPointPolicy(any())).thenReturn(pointPolicyRequest.toEntity());
		mockMvc.perform(post("/api/point-policies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pointPolicyRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}

	@Test
	void updateByAppJsonTest() throws Exception {
		when(pointPolicyService.updatePointPolicy(anyLong(), any())).thenReturn(pointPolicyRequest.toEntity());
		mockMvc.perform(put("/api/point-policies/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pointPolicyRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}

	@Test
	void deleteTest() throws Exception {
		mockMvc.perform(delete("/api/point-policies/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)));
	}
}
