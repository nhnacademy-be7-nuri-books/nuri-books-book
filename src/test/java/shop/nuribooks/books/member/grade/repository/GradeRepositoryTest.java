package shop.nuribooks.books.member.grade.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.grade.entity.Grade;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class GradeRepositoryTest {

	@Autowired
	private GradeRepository gradeRepository;

	@DisplayName("입력된 등급명으로 등급 존재 여부 확인")
	@Test
	void existsByName() {
		//given
		Grade savedGrade = getSavedGrade();

		//when
		boolean exists = gradeRepository.existsByName(savedGrade.getName());

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("입력된 등급명으로 등급 조회")
	@Test
	void findByName() {
		//given
		Grade savedGrade = getSavedGrade();

		//when
		Optional<Grade> foundGrade = gradeRepository.findByName(savedGrade.getName());

		//then
		assertThat(foundGrade).isPresent();
		assertThat(foundGrade.get().getName()).isEqualTo(savedGrade.getName());
		assertThat(foundGrade.get().getPointRate()).isEqualTo(savedGrade.getPointRate());
		assertThat(foundGrade.get().getRequirement()).isEqualTo(savedGrade.getRequirement());
	}

	/**
	 * 테스트를 위해 repository에 grade 저장 후 반환
	 */
	private Grade getSavedGrade() {
		Grade newGrade = Grade.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();

		return gradeRepository.save(newGrade);
	}
}
