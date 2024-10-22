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
		try {
			ContributorRoleEnum roleEnum = ContributorRoleEnum.valueOf(req.getName().toUpperCase());

			if (contributorRolesRepository.findByName(roleEnum).isPresent()) {
				throw new DuplicateEntityException(
					"Contributor role '" + req.getName() + "' already exists."
				);
			}

			ContributorRole savedRole = new ContributorRole();
			savedRole.setName(roleEnum);

			contributorRolesRepository.save(savedRole);
		} catch (IllegalArgumentException e) {
			String errorMessage = "Invalid contributor role name: " + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);

		}

	}

	@Override
	public List<ContributorRole> getContributorRoles() {
		return contributorRolesRepository.findAll().stream().toList();
	}

	@Override
	public void updateContributorRole(String roleName, ContributorRoleReq req) {
		String updatedRoleName = req.getName().toUpperCase();

		// 기존 역할 이름 조회
		ContributorRole existedRole = contributorRolesRepository.findByName(ContributorRoleEnum.valueOf(roleName.toUpperCase()))
			.orElseThrow(() -> new ContributorRoleNotFoundException("ContributorRole not found"));

		try {
			// 수정할 역할 이름
			ContributorRoleEnum newRoleEnum = ContributorRoleEnum.valueOf(updatedRoleName);

			// 중복 확인
			if (contributorRolesRepository.findByName(newRoleEnum).isPresent() && !existedRole.getName().equals(newRoleEnum)) {
				throw new DuplicateEntityException(
					"Contributor role '" + updatedRoleName + "' already exists."
				);
			}

			existedRole.setName(newRoleEnum);
			contributorRolesRepository.save(existedRole);
		} catch (IllegalArgumentException e) {
			String errorMessage = "Invalid contributor role name: " + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);
		}

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
