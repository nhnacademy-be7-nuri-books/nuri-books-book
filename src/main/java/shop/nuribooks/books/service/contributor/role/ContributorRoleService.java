package shop.nuribooks.books.service.contributor.role;

import java.util.List;

import shop.nuribooks.books.dto.contributor.role.ContributorRoleRequest;
import shop.nuribooks.books.dto.contributor.role.ContributorRoleResponse;
import shop.nuribooks.books.entity.book.ContributorRole;

public interface ContributorRoleService {
	ContributorRoleResponse registerContributorRole(ContributorRoleRequest req);

	List<ContributorRole> getContributorRoles();

	ContributorRoleResponse updateContributorRole(String roleName, ContributorRoleRequest req);

	void deleteContributorRole(String roleName);

}
