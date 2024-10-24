package shop.nuribooks.books.entity.book;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "books")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "book_state_id", nullable = false)
	private BookState stateId;

	@ManyToOne
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisherId;

	@Column(nullable = false, length = 50)
	private String title;

	@NotNull
	private String thumbnailImageUrl;

	private String detailImageUrl;

	@NotNull
	private LocalDate publicationDate;

	@NotNull
	private BigDecimal price;

	@NotNull
	@Min(0)
	@Max(100)
	private int discountRate;

	@NotNull
	private String description;

	@NotNull
	private String contents;

	@Column(nullable = false, length = 20)
	private String isbn;

	//TODO: Profile 어노테이션을 사용 OR 운영환경 설정 시 mysql 셋팅 후 주석 해제
	@ColumnDefault("false")
	@Column(nullable = false)
	//@Column(nullable = false, columnDefinition = "tinyint(1) default 0")
	private boolean isPackageable;

	@NotNull
	//@ColumnDefault("0")
	private int likeCount;

	@NotNull
	@Min(0)
	//@ColumnDefault("0")
	private int stock;

	@NotNull
	//@ColumnDefault("0")
	private Long viewCount;

	@Builder
	@Jacksonized
	private Book(BookState stateId, Publisher publisherId, String title, String thumbnailImageUrl,
		String detailImageUrl, LocalDate publicationDate, BigDecimal price, int discountRate,
		String description, String contents, String isbn, boolean isPackageable, int stock,
		int likeCount, Long viewCount) {
		this.stateId = stateId;
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
	}

	public BookEditor.BookEditorBuilder toEditor() {
		return BookEditor.builder().stateId(this.stateId);
	}

	public void edit(BookEditor editor) {
		this.stateId = editor.getStateId();
	}
}

