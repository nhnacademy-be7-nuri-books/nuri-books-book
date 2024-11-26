package shop.nuribooks.books.book.book.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.review.entity.Review;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "books")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisherId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookStateEnum state;

	@Column(nullable = false, length = 100)
	@NotBlank
	@Size(min = 1, max = 100)
	private String title;

	@NotBlank
	private String thumbnailImageUrl;

	private String detailImageUrl;

	@NotNull
	private LocalDate publicationDate;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true)
	@Column(precision = 8)
	private BigDecimal price;

	@NotNull
	@Min(0)
	@Max(100)
	private int discountRate;

	@NotBlank
	@Lob
	@Column(columnDefinition = "TEXT")
	private String description;

	@NotBlank
	private String contents;

	@Column(nullable = false, length = 20)
	@NotBlank
	private String isbn;

	@ColumnDefault("false")
	@Column(nullable = false)
	private boolean isPackageable;

	@ColumnDefault("0")
	private int likeCount;

	@ColumnDefault("0")
	private int stock;

	@ColumnDefault("0")
	private Long viewCount;

	private LocalDateTime deletedAt;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
	private List<Review> review;

	@ColumnDefault("0")
	private int salesCount;

	@Builder
	@Jacksonized
	private Book(Publisher publisherId, BookStateEnum state, String title, String thumbnailImageUrl,
		String detailImageUrl, LocalDate publicationDate, BigDecimal price, int discountRate,
		String description, String contents, String isbn, boolean isPackageable, int stock,
		int likeCount, Long viewCount, int salesCount) {
		this.state = state;
		this.publisherId = publisherId;
		this.title = title;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.detailImageUrl = detailImageUrl;
		this.publicationDate = publicationDate;
		this.price = price;
		this.discountRate = discountRate;
		this.description = description;
		this.contents = contents;
		this.isbn = isbn;
		this.isPackageable = isPackageable;
		this.stock = stock;
		this.likeCount = likeCount;
		this.viewCount = viewCount;
		this.salesCount = salesCount;
	}

	public void updateBookDetails(BookUpdateRequest request) {
		this.price = request.price();
		this.discountRate = request.discountRate();
		this.stock = request.stock();
		this.state = BookStateEnum.fromStringKor(request.state());
		this.thumbnailImageUrl = request.thumbnailImageUrl();
		this.detailImageUrl = request.detailImageUrl();
		this.description = request.description();
		this.contents = request.contents();
		this.isPackageable = request.isPackageable();
	}

	public void incrementViewCount() {
		this.viewCount++;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}

	public void updateStock(int count) {
		this.stock -= count;
		if (this.stock <= 0) {
			this.state = BookStateEnum.SOLD_OUT;
			this.stock = 0;
		}
	}

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		this.likeCount--;
	}
}

