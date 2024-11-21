package shop.nuribooks.books.order.wrapping.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import shop.nuribooks.books.order.wrapping.Service.WrappingPaperService;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperRequest;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/wrapping-papers")
public class WrappingPaperAdminController {
	private final WrappingPaperService wrappingPaperService;

	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping
	public ResponseEntity<Page<WrappingPaperResponse>> getWrappingPapers(Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(wrappingPaperService.getWrappingPaperResponse(pageable));
	}

	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerWrappingPaper(
		@Valid @RequestBody WrappingPaperRequest wrappingPaperRequest
	) {
		wrappingPaperService.registerWrappingPaper(wrappingPaperRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "포장지 등록 성공"));
	}

	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{wrapping-paper-id}")
	public ResponseEntity<ResponseMessage> updateWrappingPaper(
		@Valid @RequestBody WrappingPaperRequest wrappingPaperRequest,
		@PathVariable("wrapping-paper-id") Long id
	) {
		wrappingPaperService.updateWrappingPaper(id, wrappingPaperRequest);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "포장지 수정 성공"));
	}

	@HasRole(role = AuthorityType.ADMIN)
	@DeleteMapping("/{wrapping-paper-id}")
	public ResponseEntity<ResponseMessage> removeWrappingPaper(
		@PathVariable("wrapping-paper-id") Long id
	) {
		wrappingPaperService.deleteWrappingPaper(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "포장지가 삭제 성공"));
	}
}

