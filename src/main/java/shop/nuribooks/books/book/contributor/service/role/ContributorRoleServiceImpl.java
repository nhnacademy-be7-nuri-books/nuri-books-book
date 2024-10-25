package shop.nuribooks.books.book.contributor.service.role;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleRequest;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleResponse;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRole;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRoleEnum;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;
import shop.nuribooks.books.exception.contributor.DuplicateEntityException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;

@Service
@RequiredArgsConstructor
public class ContributorRoleServiceImpl implements ContributorRoleService {

	private final ContributorRoleRepository contributorRolesRepository;

	@Override
	public ContributorRoleResponse registerContributorRole(ContributorRoleRequest req) {
		ContributorRoleEnum roleEnum;

		try {
			roleEnum = ContributorRoleEnum.valueOf(req.getName().toUpperCase());

		} catch (IllegalArgumentException e) {
			String errorMessage = "Invalid contributor role name: " + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);

		}

		if (contributorRolesRepository.existsByName(roleEnum)) {
			throw new DuplicateEntityException("이미 존재하는 역할입니다.");
		}

		ContributorRole savedRole = new ContributorRole();
		savedRole.setName(roleEnum);
		contributorRolesRepository.save(savedRole);
		return new ContributorRoleResponse(savedRole.getName().name());

	}

	@Override
	public List<ContributorRole> getContributorRoles() {
		return contributorRolesRepository.findAll().stream().toList();
	}

	@Override
	public ContributorRoleResponse updateContributorRole(String roleName, ContributorRoleRequest req) {
		String updatedRoleName = req.getName().toUpperCase();

		ContributorRole existedRole = contributorRolesRepository.findByName(
				ContributorRoleEnum.valueOf(roleName.toUpperCase()))
			.orElseThrow(() -> new ContributorRoleNotFoundException("역할이 존재하지 않습니다."));

		ContributorRoleEnum newRoleEnum;
		try {
			newRoleEnum = ContributorRoleEnum.valueOf(updatedRoleName);

		} catch (IllegalArgumentException e) {
			String errorMessage = "잘못된 형식입니다. :" + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);
		}

		if (contributorRolesRepository.existsByName(newRoleEnum)) {
			throw new DuplicateEntityException("이미 존재하는 역할입니다.");
		}

		existedRole.setName(newRoleEnum);
		contributorRolesRepository.save(existedRole);
		return new ContributorRoleResponse(existedRole.getName().name());

	}

	@Override
	public void deleteContributorRole(String roleName) {
		try {
			ContributorRoleEnum roleEnum = ContributorRoleEnum.valueOf(roleName.toUpperCase());

			ContributorRole roles = contributorRolesRepository.findByName(roleEnum)
				.orElseThrow(
					() -> new ContributorRoleNotFoundException("역할이 존재하지 않습니다."));

			contributorRolesRepository.delete(roles);
		} catch (IllegalArgumentException e) {
			throw new InvalidContributorRoleException("잘못된 형식입니다. :" + roleName, e);
		}

	}
}
