package shop.nuribooks.books.book.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.point.enums.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupon_policies")
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_policy_id", nullable = false)
    private Long id;

    @NotNull
    private PolicyType policyType;

    @NotNull
    @Size(min = 2, max = 20)
    private String name;

    @NotNull
    private Long discount;

    @NotNull
    private BigDecimal minimumOrderPrice;

    @NotNull
    private BigDecimal maximumDiscountPrice;

    @NotNull
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
}
