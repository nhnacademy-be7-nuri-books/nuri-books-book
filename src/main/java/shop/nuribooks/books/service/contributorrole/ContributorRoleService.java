package shop.nuribooks.books.service.contributorrole;

import java.util.List;

import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.book.ContributorRoles;

public interface ContributorRoleService {
	void registerContributorRole(ContributorRoleReq req);
	List<ContributorRoles> getContributorRoles();

	void updateContributorRole(String roleName, ContributorRoleReq req);


}
