package shop.nuribooks.books.entity;

import java.math.BigDecimal;
import java.sql.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	@Column(nullable = false, length = 50)
	private String title;

	@Column(nullable = false)
	private String thumbnailImageUrl;

	private String detailImageUrl;

	@Column(nullable = false)
	private Date publicationDate;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private int discountRate;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String contents;

	@Column(nullable = false, length = 20)
	private String isbn;

	@Column(nullable = false, columnDefinition = "tinyint(1) default 0")
	private boolean isPackageable;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int likeCount;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int stock;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Long viewCount;
}
