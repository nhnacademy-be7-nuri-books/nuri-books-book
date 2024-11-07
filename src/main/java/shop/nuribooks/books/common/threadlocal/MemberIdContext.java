package shop.nuribooks.books.common.threadlocal;

import org.springframework.stereotype.Component;

@Component
public class MemberIdContext {
    private static final ThreadLocal<Long> memberThreadLocal = new ThreadLocal<>();

    public static void setMemberId(Long memberId) {
        memberThreadLocal.set(memberId);
    }

    public static Long getMemberId() {
        return memberThreadLocal.get();
    }

    public static void clear() {
        memberThreadLocal.remove();
    }
}
