package shop.nuribooks.books.service.contributor.role;

import java.util.List;

import shop.nuribooks.books.dto.contributor.role.ContributorRoleReqDto;
import shop.nuribooks.books.entity.book.ContributorRole;

public interface ContributorRoleService {
	void registerContributorRole(ContributorRoleReqDto req);

	List<ContributorRole> getContributorRoles();

	void updateContributorRole(String roleName, ContributorRoleReqDto req);

	void deleteContributorRole(String roleName);

}
