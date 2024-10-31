package shop.nuribooks.books.member.grade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.grade.entity.Grade;

/**
 * @author Jprotection
 */
public interface GradeRepository extends JpaRepository<Grade, Integer> {

	boolean existsByName(String name);

	Optional<Grade> findByName(String name);
}
