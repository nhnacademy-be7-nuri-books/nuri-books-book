package shop.nuribooks.books.member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.member.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
