package shop.nuribooks.books.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByUserId(String userId);

	Optional<Member> findByUserId(String userId);
}
