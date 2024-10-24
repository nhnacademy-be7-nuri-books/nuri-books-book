package shop.nuribooks.books.entity.book;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "book_states")
public class BookState {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private BookStateEnum detail;

	private BookState(BookStateEnum detail) {
		this.detail = detail;
	}

	public static BookState of(BookStateEnum detail) {
		return new BookState(detail);
	}
}

