package shop.nuribooks.books.entity.member;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NotBlank
@AllArgsConstructor
@NoArgsConstructor
public class ResignedMember {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Size(max = 20)
	private String userId;
}
