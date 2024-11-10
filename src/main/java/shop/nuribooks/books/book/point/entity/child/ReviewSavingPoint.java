package shop.nuribooks.books.book.point.entity.child;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.review.entity.Review;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_saving_point")
public class ReviewSavingPoint {
	@Id
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "point_history_id")
	private PointHistory pointHistory;

	@OneToOne
	@JoinColumn(name = "review_id")
	private Review review;
}
