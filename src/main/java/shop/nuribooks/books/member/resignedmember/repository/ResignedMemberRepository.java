package shop.nuribooks.books.member.resignedmember.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.resignedmember.entity.ResignedMember;

public interface ResignedMemberRepository extends JpaRepository<ResignedMember, Long> {
}
