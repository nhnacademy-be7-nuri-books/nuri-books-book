package shop.nuribooks.books.book.contributor.service.role;

import java.util.List;

import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleRequest;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleResponse;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRole;

public interface ContributorRoleService {
	ContributorRoleResponse registerContributorRole(ContributorRoleRequest req);

	List<ContributorRole> getContributorRoles();

	ContributorRoleResponse updateContributorRole(String roleName, ContributorRoleRequest req);

	void deleteContributorRole(String roleName);

}
