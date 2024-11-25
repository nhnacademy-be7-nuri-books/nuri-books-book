package shop.nuribooks.books.book.review.entity;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.review.dto.request.ReviewUpdateRequest;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "reviews")
@ToString
public class Review {
	@Id
	@Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String title;

	@NotBlank
	private String content;

	@NotNull
	@ColumnDefault("0")
	@Min(value = 1, message = "별점은 1 이상이어야합니다.")
	@Max(value = 5, message = "별점은 5 이하여야합니다.")
	@Column(nullable = false)
	//@Column(nullable = false, columnDefinition = "tinyint(1)")
	private int score; //평점 1-5점

	@NotNull
	private LocalDateTime createdAt;

	private LocalDateTime updateAt;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Member member;

	@ManyToOne
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@OneToOne
	@JoinColumn(name = "order_detail_id", nullable = false)
	private OrderDetail orderDetail;

	// 조회 시 사용될듯?
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
	private List<ReviewImage> reviewImages;

	@Builder
	private Review(String title, String content, int score, Member member, Book book, OrderDetail orderDetail) {
		this.title = title;
		this.content = content;
		this.score = score;
		this.createdAt = LocalDateTime.now();
		this.member = member;
		this.book = book;
		this.orderDetail = orderDetail;
		this.reviewImages = new LinkedList<>();
	}

	/**
	 * 기존 리뷰 업데이트 처리해주는 함수
	 */
	public void update(ReviewUpdateRequest reviewUpdateRequest) {
		this.title = reviewUpdateRequest.title();
		this.content = reviewUpdateRequest.content();
		this.score = reviewUpdateRequest.score();
		this.updateAt = LocalDateTime.now();
	}
}
