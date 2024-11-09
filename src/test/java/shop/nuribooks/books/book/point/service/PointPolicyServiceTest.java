package shop.nuribooks.books.book.point.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.impl.PointPolicyServiceImpl;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;

@ExtendWith(MockitoExtension.class)
public class PointPolicyServiceTest {
	@InjectMocks
	private PointPolicyServiceImpl pointPolicyService;

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	private PointPolicy pointPolicy;
	private PointPolicyRequest pointPolicyRequest;

	@BeforeEach
	public void setUp() {
		this.pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "임시", BigDecimal.valueOf(100));
		this.pointPolicy = pointPolicyRequest.toEntity();

		ReflectionTestUtils.setField(pointPolicy, "id", 1);
	}

	@Test
	public void registerPointPolicyAlready() {
		when(pointPolicyRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
		assertThrows(ResourceAlreadyExistException.class,
			() -> this.pointPolicyService.registerPointPolicy(pointPolicyRequest));
	}

	@Test
	public void registerPointPolicy() {
		when(pointPolicyRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
		when(pointPolicyRepository.save(any())).thenReturn(pointPolicy);
		assertEquals(pointPolicy, this.pointPolicyService.registerPointPolicy(pointPolicyRequest));
	}
}
