package shop.nuribooks.books.book.tag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false)
	private String name;

	@Builder
	public Tag(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public TagEditor.TagEditorBuilder toEditor() {
		return TagEditor.builder().name(name);
	}

	public void edit(TagEditor editor) {
		name = editor.getName();
	}
}
