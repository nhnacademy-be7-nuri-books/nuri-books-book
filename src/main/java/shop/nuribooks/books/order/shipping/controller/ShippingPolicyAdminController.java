package shop.nuribooks.books.order.shipping.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.service.ShippingPolicyService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shipping-policies")
public class ShippingPolicyAdminController {
	private final ShippingPolicyService shippingPolicyService;

	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping
	public ResponseEntity<Page<ShippingPolicyResponse>> getShippingPolicies(Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(shippingPolicyService.getShippingPolicyResponses(pageable));
	}

	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerShippingPolicy(
		@Valid @RequestBody ShippingPolicyRequest shippingPolicyRequest
	) {
		shippingPolicyService.registerShippingPolicy(shippingPolicyRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.OK.value(), "배송비 정책 생성 성공"));
	}

	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{shipping_policy_id}")
	public ResponseEntity<ResponseMessage> updateShippingPolicy(
		@PathVariable("shipping_policy_id") Long id,
		@Valid @RequestBody ShippingPolicyRequest shippingPolicyRequest
	) {
		shippingPolicyService.updateShippingPolicy(id, shippingPolicyRequest);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(HttpStatus.OK.value(), "배송비 정책 업데이트 성공"));
	}

	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{shipping_policy_id}/expire")
	public ResponseEntity<ResponseMessage> expireShippingPolicy(
		@PathVariable("shipping_policy_id") Long id
	) {
		shippingPolicyService.expireShippingPolicy(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "배송비 정책 삭제 성공"));
	}
}
