package shop.nuribooks.books.book;

import java.lang.reflect.Field;

public class TestUtils {

    public static void setIdForEntity(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id"); // 엔티티의 id 필드를 찾음
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("ID 설정에 실패했습니다.", e);
        }
    }
}
