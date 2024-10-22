package shop.nuribooks.books.service.contributorrole;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.book.ContributorRole;
import shop.nuribooks.books.entity.book.ContributorRoleEnum;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;
import shop.nuribooks.books.exception.contributor.DuplicateEntityException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;
import shop.nuribooks.books.repository.contributorrole.ContributorRoleRepository;

@Service
@RequiredArgsConstructor
public class ContributorRoleServiceImpl implements ContributorRoleService {

	private final ContributorRoleRepository contributorRolesRepository;

	@Override
	public void registerContributorRole(ContributorRoleReq req) {
		ContributorRoleEnum roleEnum;

		try {
			 roleEnum = ContributorRoleEnum.valueOf(req.getName().toUpperCase());

		} catch (IllegalArgumentException e) {
			String errorMessage = "Invalid contributor role name: " + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);

		}

		if (contributorRolesRepository.existsByContributorRoleEnum(roleEnum)) {
			throw new DuplicateEntityException(
				"Contributor role '" + req.getName() + "' already exists."
			);
		}

		ContributorRole savedRole = new ContributorRole();
		savedRole.setName(roleEnum);
		contributorRolesRepository.save(savedRole);

	}

	@Override
	public List<ContributorRole> getContributorRoles() {
		return contributorRolesRepository.findAll().stream().toList();
	}

	@Override
	public void updateContributorRole(String roleName, ContributorRoleReq req) {
		String updatedRoleName = req.getName().toUpperCase();

		ContributorRole existedRole = contributorRolesRepository.findByName(ContributorRoleEnum.valueOf(roleName.toUpperCase()))
			.orElseThrow(() -> new ContributorRoleNotFoundException("ContributorRole not found"));

		ContributorRoleEnum newRoleEnum;
		try {
			newRoleEnum = ContributorRoleEnum.valueOf(updatedRoleName);

		} catch (IllegalArgumentException e) {
			String errorMessage = "Invalid contributor role name: " + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);
		}

		if (contributorRolesRepository.existsByContributorRoleEnum(newRoleEnum)) {
			throw new DuplicateEntityException(
				"Contributor role '" + updatedRoleName + "' already exists."
			);
		}

		existedRole.setName(newRoleEnum);
		contributorRolesRepository.save(existedRole);

	}

	@Override
	public void deleteContributorRole(String roleName) {
		try {
			ContributorRoleEnum roleEnum = ContributorRoleEnum.valueOf(roleName.toUpperCase());

			ContributorRole roles = contributorRolesRepository.findByName(roleEnum)
				.orElseThrow(() -> new ContributorRoleNotFoundException("Contributor role '" + roleName + "' not found"));

			contributorRolesRepository.delete(roles);
		} catch (IllegalArgumentException e) {
			throw new InvalidContributorRoleException("Invalid contributor role name: " + roleName, e);
		}

	}
}
