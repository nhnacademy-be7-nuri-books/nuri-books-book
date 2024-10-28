package shop.nuribooks.books.member.resignedmember.entity;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResignedMember {

	@Id @GeneratedValue(strategy = IDENTITY)
	@Column(name = "resigned_member_id")
	private Long id;

	private String resignedUserId;
}
