package shop.nuribooks.books.service.contributorrole;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.ContributorRoles;
import shop.nuribooks.books.entity.ContributorRolesEnum;
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
}
