package shop.nuribooks.books.service.contributorrole;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.book.ContributorRoles;
import shop.nuribooks.books.entity.book.ContributorRolesEnum;
import shop.nuribooks.books.exception.contributor.DuplicateContributorRoleException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;
import shop.nuribooks.books.repository.contributorrole.ContributorRoleRepository;

@Service
@RequiredArgsConstructor
public class ContributorRoleServiceImpl implements ContributorRoleService {

	private final ContributorRoleRepository contributorRolesRepository;

	@Override
	public void registerContributorRole(ContributorRoleReq req) {
		try {
			ContributorRolesEnum roleEnum = ContributorRolesEnum.valueOf(req.getName().toUpperCase());

			if (contributorRolesRepository.findByName(roleEnum).isPresent()) {
				throw new DuplicateContributorRoleException(
					"Contributor role '" + req.getName() + "' already exists."
				);
			}

			ContributorRoles savedRole = new ContributorRoles();
			savedRole.setName(roleEnum);

			contributorRolesRepository.save(savedRole);
		} catch (IllegalArgumentException e) {
			String errorMessage = "Invalid contributor role name: " + req.getName();
			throw new InvalidContributorRoleException(errorMessage, e);

		}

	}

	@Override
	public List<ContributorRoles> getContributorRoles() {
		return contributorRolesRepository.findAll().stream().toList();
	}

	@Override
	public void updateContributorRole(String roleName, ContributorRoleReq req) {
		String updatedRoleName = req.getName().toUpperCase();

		// 기존 역할 이름 조회
		ContributorRoles existedRole = contributorRolesRepository.findByName(ContributorRolesEnum.valueOf(roleName.toUpperCase()))
			.orElseThrow(() -> new EntityNotFoundException("ContributorRole not found"));

		try {
			// 수정할 역할 이름
			ContributorRolesEnum newRoleEnum = ContributorRolesEnum.valueOf(updatedRoleName);

			// 중복 확인
			if (contributorRolesRepository.findByName(newRoleEnum).isPresent() && !existedRole.getName().equals(newRoleEnum)) {
				throw new DuplicateContributorRoleException(
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
}
