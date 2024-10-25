package shop.nuribooks.books.member.authority.entity;

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
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ADMIN, MEMBER, SELLER
     */
    @Enumerated(EnumType.STRING)
    private AuthorityType authorityType;

    @Builder
    private Authority(AuthorityType authorityType) {
        this.authorityType = authorityType;
    }

    public void editAuthority(AuthorityEditRequest request) {
        this.authorityType = request.authorityType();
    }
}
