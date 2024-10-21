package shop.nuribooks.books.entity;

import java.math.BigDecimal;
import java.sql.Date;
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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "books")
public class Books {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "book_state_id", nullable = false)
	private BookStates stateId;

	@ManyToOne
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publishers publisherId;

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
	private int discountRate;

	@NotNull
	private String description;

	@NotNull
	private String contents;

	@Column(nullable = false, length = 20)
	private String isbn;

	@ColumnDefault("false")
	@Column(nullable = false)

	//TODO: Profile 어노테이션을 사용 OR 운영환경 설정 시 mysql 셋팅 후 주석 해제
	//@Column(nullable = false, columnDefinition = "tinyint(1)")
	private boolean isPackageable;

	@NotNull
	//@ColumnDefault("0")
	private int likeCount = 0;

	@NotNull
	//@ColumnDefault("0")
	private int stock = 0;

	@NotNull
	//@ColumnDefault("0")
	private Long viewCount = 0L;
}

