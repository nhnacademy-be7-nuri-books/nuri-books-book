package shop.nuribooks.books.order.shipping.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.shipping.dto.ShippingResponse;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.exception.CanNotDeliveryException;
import shop.nuribooks.books.order.shipping.exception.ShippingNotFoundException;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;
import shop.nuribooks.books.order.shipping.service.ShippingAdminService;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ShippingAdminServiceImpl implements ShippingAdminService {
	private final ShippingRepository shippingRepository;
	private final OrderDetailRepository orderDetailRepository;

	@Override
	public Page<ShippingResponse> getShippingResponses(Pageable pageable) {
		return shippingRepository.findAll(pageable).map(Shipping::toResponseDto);
	}

	@Override
	public ShippingResponse getShippingResponse(Long id) {
		return shippingRepository.findById(id).orElseThrow(ShippingNotFoundException::new).toResponseDto();
	}

	@Override
	@Transactional
	public void updateDeliveryStatus(Long id) {
		Shipping shipping = shippingRepository.findById(id).orElseThrow(ShippingNotFoundException::new);
		shipping.startDelivery();
		List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(shipping.getOrder().getId());
		for (OrderDetail orderDetail : orderDetailList) {
			if (orderDetail.getOrderState() == OrderState.PAID) {
				orderDetail.setOrderState(OrderState.DELIVERING);
			} else {
				throw new CanNotDeliveryException("배송 불가능한 상태의 주문이 존재합니다.");
			}
		}
	}

	@Override
	@Transactional
	public void completeDelivery(Long id) {
		Shipping shipping = shippingRepository.findById(id).orElseThrow(ShippingNotFoundException::new);
		shipping.completeDelivery();
		List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(shipping.getOrder().getId());
		for (OrderDetail orderDetail : orderDetailList) {
			if (orderDetail.getOrderState() == OrderState.DELIVERING) {
				orderDetail.setOrderState(OrderState.COMPLETED);
			} else {
				throw new CanNotDeliveryException("배송 완료가 되기 부적절한 주문 건이 있습니다.");
			}
		}
	}
}
