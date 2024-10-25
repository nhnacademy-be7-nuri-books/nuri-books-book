package shop.nuribooks.books.member.authority.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.entity.AuthorityEnum;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    boolean existsByName(AuthorityEnum authorityEnum);
}
