package shop.nuribooks.books.book.coupon.categorycoupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.coupon.entity.Coupon;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "category_coupons")
@Entity
public class CategoryCoupon {
	@Id
	private Long id;

	@MapsId
	@OneToOne
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Builder
	CategoryCoupon(Coupon coupon, Category category) {
		this.coupon = coupon;
		this.category = category;
	}
}
