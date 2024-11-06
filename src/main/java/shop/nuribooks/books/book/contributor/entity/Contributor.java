package shop.nuribooks.books.book.contributor.entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "contributors")
public class Contributor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Length(min = 1, max = 50)
	private String name;

	@Builder
	public Contributor(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public ContributorEditor.ContributorEditorBuilder toEditor() {
		return ContributorEditor.builder()
			.name(name);
	}

	public void edit(ContributorEditor editor) {
		name = editor.getName();
	}

}
