package shop.nuribooks.books.repository.contributorrole;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.ContributorRoles;
import shop.nuribooks.books.entity.book.ContributorRolesEnum;

public interface ContributorRoleRepository extends JpaRepository<ContributorRoles, Long> {
	Optional<ContributorRoles> findByName(ContributorRolesEnum name);

}
