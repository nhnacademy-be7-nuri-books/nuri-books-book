package shop.nuribooks.books.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;

public class MemberIdFilter implements Filter {

    private static final String X_USER_ID = "X-USER-ID";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String headerMemberId = request.getHeader(X_USER_ID);

        Long memberId;

        try {
            memberId = Long.parseLong(headerMemberId);
        } catch (NumberFormatException e) {
            memberId = null;
        }

        MemberIdContext.setMemberId(memberId);

        filterChain.doFilter(servletRequest, servletResponse);

        MemberIdContext.clear();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
