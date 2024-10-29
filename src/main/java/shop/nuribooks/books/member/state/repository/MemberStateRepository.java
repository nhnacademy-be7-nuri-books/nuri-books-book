package shop.nuribooks.books.member.state.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.nuribooks.books.member.state.entity.MemberState;
import shop.nuribooks.books.member.state.entity.MemberStateType;

public interface MemberStateRepository extends JpaRepository<MemberState, Long> {

    boolean existsByMemberStateType(MemberStateType memberStateType);
}
