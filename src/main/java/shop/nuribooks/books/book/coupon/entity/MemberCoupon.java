package shop.nuribooks.books.book.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.member.member.entity.Member;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_coupons")
public class MemberCoupon {

    @Id
    @Column(name = "member_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Member member;

    @NotNull
    private boolean isUsed = false;

    @NotNull
    private LocalDate createdAt;

    @NotNull
    private LocalDate expiredAt;
}
