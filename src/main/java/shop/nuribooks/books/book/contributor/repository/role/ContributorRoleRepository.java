package shop.nuribooks.books.book.contributor.repository.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;

public interface ContributorRoleRepository extends JpaRepository<ContributorRole, Long> {
	Optional<ContributorRole> findByName(ContributorRoleEnum name);

	boolean existsByName(ContributorRoleEnum name);
}
