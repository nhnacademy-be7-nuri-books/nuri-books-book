package shop.nuribooks.books.common.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@RequiredArgsConstructor
@Aspect
@Component
public class AuthorizationAspect {

    private final MemberRepository memberRepository;

    @Around("@annotation(hasRole)")
    public Object logRequestHeaders(ProceedingJoinPoint joinPoint, HasRole hasRole) throws Throwable {

        Long memberId = MemberIdContext.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("잘못된 요청입니다"));
        AuthorityType role = hasRole.role();

        if (member.getAuthority() != role) {
            throw new BadRequestException("잘못된 요청입니다");
        }

        return joinPoint.proceed();
    }
}
