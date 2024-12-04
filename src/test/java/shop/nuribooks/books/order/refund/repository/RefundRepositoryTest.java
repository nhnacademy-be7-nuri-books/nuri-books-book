package shop.nuribooks.books.order.refund.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.request.register.OrderSavingPointRequest;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.refund.dto.RefundInfoDto;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;

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

	@Autowired
	private ShippingPolicyRepository shippingPolicyRepository;

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private RefundRepository refundRepository;

	@DisplayName("결제 금액, 적립된 포인트, 출고일은 가져온다.")
	@Test
	void findRefundInfo() {
		// given
		Customer customer = TestUtils.createCustomer();
		Grade grade = TestUtils.creategrade();
		gradeRepository.save(grade);
		Member member = TestUtils.createMember(customer, grade);
		memberRepository.save(member);

		PointPolicyRequest ppr = new PointPolicyRequest(PolicyType.FIXED, "이름", BigDecimal.ONE);
		PointPolicy pointPolicy = pointPolicyRepository.save(ppr.toEntity());

		Order order = TestUtils.createOrder(customer);
		orderRepository.save(order);

		OrderSavingPointRequest orderSavingPointRequest = new OrderSavingPointRequest(member, order,
			order.getPaymentPrice());
		pointHistoryRepository.save(orderSavingPointRequest.toEntity(pointPolicy));

		ShippingPolicy shippingPolicy = TestUtils.createShippingPolicy();
		shippingPolicyRepository.save(shippingPolicy);
		Shipping shipping = TestUtils.createShipping(order, shippingPolicy);
		shippingRepository.save(shipping);

		// when
		RefundInfoDto refundInfo = refundRepository.findRefundInfo(order.getId());

		// then
		assertThat(refundInfo.paymentPrice()).isEqualTo(order.getPaymentPrice().setScale(0, RoundingMode.DOWN));
		assertThat(refundInfo.orderSavingPoint().setScale(0, RoundingMode.DOWN)).isEqualTo(
			orderSavingPointRequest.toEntity(pointPolicy).getAmount());
		assertThat(refundInfo.shippingAt()).isEqualTo(shipping.getShippingAt());
	}

}
