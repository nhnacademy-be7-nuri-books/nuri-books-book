package shop.nuribooks.books.service.contributorrole;

import java.util.List;

import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.book.ContributorRole;

public interface ContributorRoleService {
	void registerContributorRole(ContributorRoleReq req);
	List<ContributorRole> getContributorRoles();

	void updateContributorRole(String roleName, ContributorRoleReq req);

	void deleteContributorRole(String roleName);



}
