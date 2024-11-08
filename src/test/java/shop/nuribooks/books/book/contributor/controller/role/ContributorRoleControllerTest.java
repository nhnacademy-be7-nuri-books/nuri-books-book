package shop.nuribooks.books.book.contributor.controller.role;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleRequest;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleResponse;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.service.role.ContributorRoleServiceImpl;

@WebMvcTest(ContributorRoleController.class)
@AutoConfigureMockMvc
class ContributorRoleControllerTest {

	@MockBean
	private ContributorRoleServiceImpl contributorRoleService;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

	}

	@DisplayName("기여자 역할 등록")
	@Test
	void registerContributorRole() throws Exception {
		// given
		ContributorRoleRequest request = new ContributorRoleRequest("AUTHOR");
		ContributorRoleResponse response = new ContributorRoleResponse("AUTHOR");

		when(contributorRoleService.registerContributorRole(any(ContributorRoleRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/contributors/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("AUTHOR"));

		verify(contributorRoleService, times(1)).registerContributorRole(any(ContributorRoleRequest.class));
	}

	@DisplayName("기여자 역할 전체 조회")
	@Test
	void getContributorRole() throws Exception{
		// given
		ContributorRole role1 = new ContributorRole();
		role1.setName(ContributorRoleEnum.AUTHOR);

		ContributorRole role2 = new ContributorRole();
		role2.setName(ContributorRoleEnum.ILLUSTRATION);

		when(contributorRoleService.getContributorRoles()).thenReturn(List.of(role1, role2));

		// when & then
		mockMvc.perform(get("/api/contributors/roles")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].name").value("AUTHOR"))
			.andExpect(jsonPath("$[1].name").value("ILLUSTRATION"));

		verify(contributorRoleService, times(1)).getContributorRoles();
	}

	@DisplayName("기여자 역할 수정")
	@Test
	void updateContributorRole() throws Exception {
		// given
		String roleName = "AUTHOR";
		ContributorRoleRequest request = new ContributorRoleRequest("EDITOR");
		ContributorRoleResponse response = new ContributorRoleResponse("EDITOR");

		when(contributorRoleService.updateContributorRole(eq(roleName), any(ContributorRoleRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(put("/api/contributors/roles/{roleName}", roleName)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("EDITOR"));

		verify(contributorRoleService, times(1)).updateContributorRole(eq(roleName), any(ContributorRoleRequest.class));
	}

	@DisplayName("기여자 역할 삭제")
	@Test
	void deleteContributorRole() throws Exception {
		// given
		String roleName = "AUTHOR";

		// when & then
		mockMvc.perform(delete("/api/contributors/roles/{roleName}", roleName)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(contributorRoleService, times(1)).deleteContributorRole(eq(roleName));
	}
}