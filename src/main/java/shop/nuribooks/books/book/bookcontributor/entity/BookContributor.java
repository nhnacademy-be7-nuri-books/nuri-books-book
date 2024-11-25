package shop.nuribooks.books.book.bookcontributor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "book_contributors")
public class BookContributor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_contributor_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contributor_id", nullable = false)
	private Contributor contributor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contributor_role_id", nullable = false)
	private ContributorRole contributorRole;

	@Builder
	public BookContributor(Book book, Contributor contributor, ContributorRole contributorRole) {
		this.book = book;
		this.contributor = contributor;
		this.contributorRole = contributorRole;
	}

}
