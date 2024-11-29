package shop.nuribooks.books.order.shipping.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.order.shipping.dto.ShippingResponse;
import shop.nuribooks.books.order.shipping.service.ShippingAdminService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/shipping")
@RestController
public class ShippingAdminController {
	private final ShippingAdminService shippingAdminService;

	@Operation(summary = "배송 목록 조회", description = "배송 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping
	public ResponseEntity<Page<ShippingResponse>> getShippingResponses(Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(shippingAdminService.getShippingResponses(pageable));
	}

	@Operation(summary = "배송 상세 조회", description = "배송 내역을 상세 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping("/{id}")
	public ResponseEntity<ShippingResponse> getShippingResponse(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(shippingAdminService.getShippingResponse(id));
	}

	@Operation(summary = "배송 상태 수정", description = "배송 상태를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{id}")
	public ResponseEntity<ResponseMessage> updateDeliveryStatus(@PathVariable Long id) {
		shippingAdminService.updateDeliveryStatus(id);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(HttpStatus.OK.value(), "배송이 시작되었습니다."));
	}

	@Operation(summary = "배송 완료 처리", description = "배송을 완료 처리합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "완료 처리 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/delivery-complete/{id}")
	public ResponseEntity<ResponseMessage> completeDelivery(@PathVariable Long id) {
		shippingAdminService.completeDelivery(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "배송 완료 처리!"));
	}
}
