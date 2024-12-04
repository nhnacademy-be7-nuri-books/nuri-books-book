package shop.nuribooks.books.order.refund.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.repository.OrderRepository;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class RefundRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PointHistoryRepository pointHistoryRepository;

	@Autowired
	private PointPolicyRepository pointPolicyRepository;
	private Member member;
	private PointPolicy pointPolicy;

	// @DisplayName("결제 금액, 적립된 포인트, 출고일은 가져온다.")
	// @Test
	// void findRefundInfo() {
	// 	// given
	// 	Customer customer = TestUtils.createCustomer();
	// 	Grade grade = TestUtils.creategrade();
	// 	gradeRepository.save(grade);
	// 	member = TestUtils.createMember(customer, grade);
	// 	memberRepository.save(member);
	//
	// 	PointPolicyRequest ppr = new PointPolicyRequest(PolicyType.FIXED, "이름", BigDecimal.ONE);
	// 	pointPolicy = pointPolicyRepository.save(ppr.toEntity());
	//
	// 	Order order = TestUtils.createOrder(customer);
	// 	orderRepository.save(order);
	//
	// 	OrderSavingPointRequest orderSavingPointRequest = new OrderSavingPointRequest(member, order, order.getPaymentPrice())
	// 	pointHistoryRepository.save(orderSavingPointRequest.toEntity(pointPolicy));
	//
	// 	Shipping shipping = new Shipping()
	//
	// 	// when
	//
	// 	// then
	// }

}
