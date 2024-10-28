package shop.nuribooks.books.member.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByUserId(String userId);

	Optional<Member> findByUserId(String userId);

	boolean existsByGradeId(Integer gradeId);
}
