package shop.nuribooks.books.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.member.Grade;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
