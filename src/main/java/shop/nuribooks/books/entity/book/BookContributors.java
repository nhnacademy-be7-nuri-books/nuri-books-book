package shop.nuribooks.books.entity.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.entity.Books;
import shop.nuribooks.books.entity.book.ContributorRoles;
import shop.nuribooks.books.entity.book.Contributors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "book_contributors")
public class BookContributors {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "book_id")
	private Books books;

	@ManyToOne
	@JoinColumn(name = "contributor_id")
	private Contributors contributors;

	@ManyToOne
	@JoinColumn(name = "contributor_role_id")
	private ContributorRoles contributorRoles;
}
