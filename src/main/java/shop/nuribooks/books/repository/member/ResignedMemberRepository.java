package shop.nuribooks.books.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.member.ResignedMember;

public interface ResignedMemberRepository extends JpaRepository<ResignedMember, Long> {
}
