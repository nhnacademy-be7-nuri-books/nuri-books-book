package shop.nuribooks.books.book.elasticsearch.docs;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Data
public class ContributorDocument {

	@Field(type = FieldType.Keyword)
	private String role;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String name;
}