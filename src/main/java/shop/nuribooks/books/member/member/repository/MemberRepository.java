package shop.nuribooks.books.member.member.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.member.entity.Member;

/**
 * @author Jprotection
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

	boolean existsByUserId(String userId);

	Optional<Member> findByUserId(String userId);

	boolean existsByGradeId(Integer gradeId);

	List<Member> findAllByLatestLoginAtBefore(LocalDateTime thresholdDate);

	List<Member> findAllByWithdrawnAtBefore(LocalDateTime thresholdDate);
}
