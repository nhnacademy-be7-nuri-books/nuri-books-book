package shop.nuribooks.books.service.contributor.role;

import java.util.List;

import shop.nuribooks.books.dto.contributor.role.ContributorRoleRequest;
import shop.nuribooks.books.entity.book.ContributorRole;

public interface ContributorRoleService {
	void registerContributorRole(ContributorRoleRequest req);

	List<ContributorRole> getContributorRoles();

	void updateContributorRole(String roleName, ContributorRoleRequest req);

	void deleteContributorRole(String roleName);

}
