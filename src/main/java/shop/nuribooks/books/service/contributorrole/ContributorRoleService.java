package shop.nuribooks.books.service.contributorrole;

import java.util.List;

import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.entity.ContributorRoles;

public interface ContributorRoleService {
	void registerContributorRole(ContributorRoleReq req);
	List<ContributorRoles> getContributorRoles();


}
