package shop.nuribooks.books.book.contributor.service.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleRequest;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleResponse;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;
import shop.nuribooks.books.exception.contributor.DuplicateEntityException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

@ExtendWith(MockitoExtension.class)
class ContributorRoleServiceImplTest {

	@Mock
	private ContributorRoleRepository contributorRolesRepository;

	@InjectMocks
	private ContributorRoleServiceImpl contributorRoleService;

	@DisplayName("기여자 역할 등록 성공")
	@Test
	void registerContributorRole() {
		// given
		ContributorRoleRequest request = new ContributorRoleRequest("author");

		// when
		when(contributorRolesRepository.existsByName(ContributorRoleEnum.AUTHOR)).thenReturn(false);
		ContributorRoleResponse response = contributorRoleService.registerContributorRole(request);

		// then
		assertNotNull(response);
		assertEquals("AUTHOR", response.getName());
		verify(contributorRolesRepository, times(1)).save(any(ContributorRole.class));
	}

	@DisplayName("기여자 역할 등록 실패 - 중복 예외 발생")
	@Test
	void failed_registerContributorRole() {
		// given
		ContributorRoleRequest request = new ContributorRoleRequest("author");

		// when
		when(contributorRolesRepository.existsByName(ContributorRoleEnum.AUTHOR)).thenReturn(true);

		// then
		assertThrows(DuplicateEntityException.class, () -> contributorRoleService.registerContributorRole(request));
		verify(contributorRolesRepository, never()).save(any(ContributorRole.class));
	}

	@DisplayName("기여자 역할 등록 실패 - 잘못된 형식일 때")
	@Test
	void failed1_registerContributorRole() {
		// given
		ContributorRoleRequest request = new ContributorRoleRequest("INVALID_ROLE_NAME");

		// when & then
		InvalidContributorRoleException exception = assertThrows(
			InvalidContributorRoleException.class,
			() -> contributorRoleService.registerContributorRole(request)
		);

		assertEquals("잘못된 형식입니다.", exception.getMessage());
	}

	@DisplayName("모든 기여자 역할 조회 성공")
	@Test
	void getContributorRoles() {
		// given
		ContributorRole role = new ContributorRole();
		role.setName(ContributorRoleEnum.AUTHOR);
		ContributorRole role1 = new ContributorRole();
		role1.setName(ContributorRoleEnum.ILLUSTRATOR);

		when(contributorRolesRepository.findAll()).thenReturn(List.of(role, role1));

		// when
		List<ContributorRole> roles = contributorRoleService.getContributorRoles();

		// then
		assertNotNull(roles);
		assertEquals(2, roles.size());
		assertEquals(ContributorRoleEnum.AUTHOR, roles.get(0).getName());
		assertEquals(ContributorRoleEnum.ILLUSTRATOR, roles.get(1).getName());

		verify(contributorRolesRepository, times(1)).findAll();
	}

	@DisplayName("기여자 역할 삭제 성공")
	@Test
	void deleteContributorRole() {
		// given
		ContributorRole existingRole = new ContributorRole();
		existingRole.setName(ContributorRoleEnum.AUTHOR);
		when(contributorRolesRepository.findByName(ContributorRoleEnum.AUTHOR)).thenReturn(Optional.of(existingRole));

		// when
		contributorRoleService.deleteContributorRole("author");

		// then
		verify(contributorRolesRepository, times(1)).delete(existingRole);
	}

	@DisplayName("기여자 역할 삭제 실패 - 역할이 없을 때 예외 발생")
	@Test
	void failed_deleteContributorRole() {
		// given
		when(contributorRolesRepository.findByName(ContributorRoleEnum.AUTHOR)).thenReturn(Optional.empty());

		// then
		assertThrows(ContributorRoleNotFoundException.class,
			() -> contributorRoleService.deleteContributorRole("author"));
	}

	@DisplayName("기여자 역할 삭제 실패 - 잘못된 형식 예외 발생")
	@Test
	void failed1_deleteContributorRole() {
		// given
		// then
		assertThrows(InvalidContributorRoleException.class,
			() -> contributorRoleService.deleteContributorRole("invalidRole"));
	}
}