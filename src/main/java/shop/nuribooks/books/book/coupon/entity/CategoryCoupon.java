package shop.nuribooks.books.book.coupon.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import shop.nuribooks.books.book.category.entity.Category;

@Entity
@DiscriminatorValue("BOOK")
@Getter
@NoArgsConstructor
@SuperBuilder
@Table(name = "category_coupons")
public class CategoryCoupon extends Coupon {
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
}
