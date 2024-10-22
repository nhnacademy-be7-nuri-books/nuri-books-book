package shop.nuribooks.books.service.contributorrole;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.ContributorRoles;
import shop.nuribooks.books.entity.book.enums.ContributorRolesEnum;
import shop.nuribooks.books.repository.contributorrole.ContributorRoleRepository;

@Service
@RequiredArgsConstructor
public class ContributorRoleServiceImpl implements ContributorRoleService {

	private final ContributorRoleRepository contributorRolesRepository;

	@Override
	public void registerContributorRole(ContributorRoleReq req) {
		ContributorRoles savedRole = new ContributorRoles();

		ContributorRolesEnum roleEnum;

		roleEnum = ContributorRolesEnum.valueOf(req.getName().toUpperCase());
		savedRole.setName(roleEnum);

		contributorRolesRepository.save(savedRole);

	}
}
