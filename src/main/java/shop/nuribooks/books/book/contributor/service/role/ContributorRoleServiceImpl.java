package shop.nuribooks.books.book.contributor.service.role;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleRequest;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleResponse;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;
import shop.nuribooks.books.exception.contributor.DuplicateEntityException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

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
			throw new InvalidContributorRoleException();

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
	public void deleteContributorRole(String roleName) {
		ContributorRoleEnum roleEnum;

		try {
			roleEnum = ContributorRoleEnum.valueOf(roleName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidContributorRoleException();
		}

		ContributorRole role = contributorRolesRepository.findByName(roleEnum)
			.orElseThrow(() -> new ContributorRoleNotFoundException("역할이 존재하지 않습니다."));

		contributorRolesRepository.delete(role);
	}
}
