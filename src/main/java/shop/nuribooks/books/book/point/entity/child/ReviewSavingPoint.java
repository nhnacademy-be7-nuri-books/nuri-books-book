package shop.nuribooks.books.book.point.entity.child;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.review.entity.Review;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("review_saving_point")
@Table(name = "review_saving_point")
public class ReviewSavingPoint extends PointHistory {
	@OneToOne
	@JoinColumn(name = "review_id")
	private Review review;
}
