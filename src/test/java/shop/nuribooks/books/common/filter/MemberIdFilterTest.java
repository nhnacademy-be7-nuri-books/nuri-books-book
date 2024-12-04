package shop.nuribooks.books.common.filter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;

class MemberIdFilterTest {

	private MemberIdFilter memberIdFilter;

	@BeforeEach
	void setUp() {
		memberIdFilter = new MemberIdFilter();
	}

	@Test
	void testDoFilterWithValidMemberId() throws ServletException, IOException {
		// Given
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getHeader("X-USER-ID")).thenReturn("123");

		// When
		memberIdFilter.doFilter(request, response, filterChain);

		// Then
		assertThat(MemberIdContext.getMemberId()).isNull();
		verify(filterChain, times(1)).doFilter(request, response);

	}

	@Test
	void testDoFilterWithInvalidMemberId() throws ServletException, IOException {
		// Given
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getHeader("X-USER-ID")).thenReturn("invalid");

		// When
		memberIdFilter.doFilter(request, response, filterChain);

		// Then
		assertThat(MemberIdContext.getMemberId()).isNull();
		verify(filterChain, times(1)).doFilter(request, response);
	}

	@Test
	void testDoFilterWithMissingHeader() throws ServletException, IOException {
		// Given
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getHeader("X-USER-ID")).thenReturn(null);

		// When
		memberIdFilter.doFilter(request, response, filterChain);

		// Then
		assertThat(MemberIdContext.getMemberId()).isNull();
		verify(filterChain, times(1)).doFilter(request, response);
	}

	@Test
	void testClearContextAfterFilter() throws ServletException, IOException {
		// Given
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getHeader("X-USER-ID")).thenReturn("123");

		// When
		memberIdFilter.doFilter(request, response, filterChain);

		// Context should be cleared after the filter execution
		assertThat(MemberIdContext.getMemberId()).isNull();
	}

	@Test
	void initTest() throws ServletException {
		FilterConfig filterConfig = new MockFilterConfig("name");
		memberIdFilter.init(filterConfig);
	}

	@Test
	void destroyTest() throws ServletException {
		memberIdFilter.destroy();
	}
}
