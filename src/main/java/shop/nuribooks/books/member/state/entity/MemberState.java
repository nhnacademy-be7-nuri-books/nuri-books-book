package shop.nuribooks.books.member.state.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.member.state.dto.request.MemberStateEditRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberStateType memberStateType;

    @Builder
    private MemberState(MemberStateType memberStateType) {
        this.memberStateType = memberStateType;
    }

    public void editMemberState(MemberStateEditRequest request) {
        this.memberStateType = request.memberStateType();
    }
}
