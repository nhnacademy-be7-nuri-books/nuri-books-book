package shop.nuribooks.books.book.point.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
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
	private PointPolicyResponse pointPolicyResponse;

	@BeforeEach
	public void setUp() {
		this.pointPolicyRequest = new PointPolicyRequest(PolicyType.FIXED, "임시", BigDecimal.valueOf(100));
		this.pointPolicy = pointPolicyRequest.toEntity();
		this.pointPolicyResponse = new PointPolicyResponse() {
			@Override
			public long getId() {
				return 1;
			}

			@Override
			public PolicyType getPolicyType() {
				return PolicyType.FIXED;
			}

			@Override
			public String getName() {
				return "이름";
			}

			@Override
			public BigDecimal getAmount() {
				return BigDecimal.valueOf(10);
			}
		};

		ReflectionTestUtils.setField(pointPolicy, "id", 1);
	}

	@Test
	public void getPointPolicyList() {
		when(pointPolicyRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(this.pointPolicyResponse));
		assertEquals(1, pointPolicyService.getPointPolicyResponses().size());
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

	@Test
	public void updatePointPolicyFail() {
		when(pointPolicyRepository.findPointPolicyByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());
		assertThrows(PointPolicyNotFoundException.class,
			() -> pointPolicyService.updatePointPolicy(2, pointPolicyRequest));
	}

	@Test
	public void updatePointPolicySuccess() {
		PointPolicyRequest ppr = new PointPolicyRequest(PolicyType.FIXED, "흠.", BigDecimal.valueOf(19));
		when(pointPolicyRepository.findPointPolicyByIdAndDeletedAtIsNull(anyLong())).thenReturn(
			Optional.of(pointPolicy));
		assertEquals(pointPolicy, pointPolicyService.updatePointPolicy(1, ppr));
	}

	@Test
	public void deletePointPolicyFail() {
		when(pointPolicyRepository.findPointPolicyByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());
		assertThrows(PointPolicyNotFoundException.class,
			() -> pointPolicyService.deletePointPolicy(2));
	}

	@Test
	public void deletePointPolicySuccess() {
		when(pointPolicyRepository.findPointPolicyByIdAndDeletedAtIsNull(anyLong())).thenReturn(
			Optional.of(pointPolicy));
		pointPolicyService.deletePointPolicy(1);
		assert (pointPolicy.getDeletedAt() != null);
	}
}