package shop.nuribooks.books.member.member.dto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DtoMapperTest {

	@DisplayName("DtoMapper 클래스는 인스턴스를 생성할 수 없으며, 기본 생성자는 예외를 던진다.")
	@Test
	void testDtoMapperConstructor() throws NoSuchMethodException {
		// 리플렉션을 이용하여 private 생성자에 접근
		Constructor<DtoMapper> constructor = DtoMapper.class.getDeclaredConstructor();
		constructor.setAccessible(true); // private 접근을 허용

		Assertions.assertThatThrownBy(constructor::newInstance)
			.isInstanceOf(InvocationTargetException.class) // InvocationTargetException이 발생해야 한다.
			.hasCauseInstanceOf(UnsupportedOperationException.class); // 내부 예외가 UnsupportedOperationException이어야 한다.
	}
}
