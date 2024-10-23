package shop.nuribooks.books.repository.contributor.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.ContributorRole;
import shop.nuribooks.books.entity.book.ContributorRoleEnum;

public interface ContributorRoleRepository extends JpaRepository<ContributorRole, Long> {
	Optional<ContributorRole> findByName(ContributorRoleEnum name);

	boolean existsByName(ContributorRoleEnum name);
}
