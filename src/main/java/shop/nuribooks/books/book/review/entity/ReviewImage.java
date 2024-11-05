package shop.nuribooks.books.book.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "review_images")
public class ReviewImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_image_id")
	private Long id;

	@NotNull
	private String imageUrl;

	@ManyToOne
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@Builder
	private ReviewImage(String imageUrl, Review review) {
		this.imageUrl = imageUrl;
		this.review = review;
	}
}
