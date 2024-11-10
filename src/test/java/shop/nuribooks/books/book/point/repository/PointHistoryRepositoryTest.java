package shop.nuribooks.books.book.point.repository;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
@Import({QuerydslConfiguration.class})
public class PointHistoryRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private GradeRepository gradeRepository;
	@Autowired
	private PointHistoryRepository PointHistoryRepository;

	@Autowired
	private PointPolicyRepository pointPolicyRepository;
	private Member member;
	private PointPolicy pointPolicy;

	private static long id = 0l;

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();
		Grade grade = TestUtils.creategrade();
		gradeRepository.save(grade);
		member = TestUtils.createMember(customer, grade);
		memberRepository.save(member);
		TestUtils.setIdForEntity(member, ++id);

	}

	@Test
	void findAllHistoriesTest() {
		PointPolicyRequest ppr = new PointPolicyRequest(PolicyType.FIXED, "이름", BigDecimal.ONE);
		pointPolicy = pointPolicyRepository.save(ppr.toEntity());
		PointHistoryRequest rspr = new PointHistoryRequest(member);
		PointHistoryRepository.save(rspr.toEntity(pointPolicy));
		PointHistoryRepository.save(rspr.toEntity(pointPolicy));
		assertEquals(2,
			PointHistoryRepository.findPointHistories(
				new PointHistoryPeriodRequest(),
				PageRequest.of(0, 2),
				id
			).size()
		);
	}

	@Test
	void findEarnedHistoriesTest() {

		PointPolicyRequest ppr = new PointPolicyRequest(PolicyType.FIXED, "이름", BigDecimal.ONE);
		pointPolicy = pointPolicyRepository.save(ppr.toEntity());
		PointHistoryRequest rspr = new PointHistoryRequest(member);
		PointHistoryRepository.save(rspr.toEntity(pointPolicy));
		PointHistoryRepository.save(rspr.toEntity(pointPolicy));
		assertEquals(2,
			PointHistoryRepository.findEarnedPointHistories(
				new PointHistoryPeriodRequest(),
				PageRequest.of(0, 2),
				id
			).size()
		);
	}

	@Test
	void findUsedHistoriesTest() {
		PointPolicyRequest ppr = new PointPolicyRequest(PolicyType.FIXED, "이름", BigDecimal.ONE);
		pointPolicy = pointPolicyRepository.save(ppr.toEntity());
		PointHistoryRequest rspr = new PointHistoryRequest(member);
		PointHistoryRepository.save(rspr.toEntity(pointPolicy));
		PointHistoryRepository.save(rspr.toEntity(pointPolicy));
		assertEquals(0,
			PointHistoryRepository.findUsedPointHistories(
				new PointHistoryPeriodRequest(),
				PageRequest.of(0, 2),
				id
			).size()
		);
	}
}
