package shop.nuribooks.books.book.coupon.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("All")
@Getter
@NoArgsConstructor
@SuperBuilder
@Table(name = "all_coupons")
public class AllCoupon extends Coupon {
}
