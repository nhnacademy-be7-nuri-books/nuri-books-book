package shop.nuribooks.books.book.publisher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "publishers")
public class Publisher {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "publisher_id")
	private Long id;

	@NotNull
	private String name;

	@Builder
	public Publisher(String name) {
		this.name = name;
	}

	public Publisher(Long id, String name) {
		this.name = name;
		this.id = id;
	}

	public PublisherEditor.PublisherEditorBuilder toEditor() {
		return PublisherEditor.builder()
			.name(name);
	}

	public void edit(PublisherEditor editor) {
		name = editor.getName();
	}
}
