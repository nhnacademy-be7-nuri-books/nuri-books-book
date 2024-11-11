package shop.nuribooks.books.book.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.DefaultValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.point.entity.PointPolicy;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private CouponType couponType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "coupon_policy_id")
    private CouponPolicy couponPolicy;

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @NotNull
    private LocalDate createdAt;

    @NotNull
    private LocalDate expiredAt;

    private int period = 0;

}
