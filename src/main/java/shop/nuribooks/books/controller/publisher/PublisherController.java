package shop.nuribooks.books.controller.publisher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;
import shop.nuribooks.books.service.publisher.PublisherService;

@RestController
@RequiredArgsConstructor
public class PublisherController {
	private final PublisherService publisherService;

	/**
	 * 출판사 이름 등록하는 controller
	 *
	 * @author kyongmin
	 * @param request 등록할 출판사 이름
	 * @return ResponseEntity
	 */
	@Operation(summary = "출판사 등록", description = "새로운 출판사를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "출판사 등록 성공"),
		@ApiResponse(responseCode = "409", description = "출판사가 이미 존재함"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@PostMapping("/api/publishers")
	public ResponseEntity<PublisherResponse> registerPublisher(@Valid @RequestBody PublisherRequest request) {
		PublisherResponse response = publisherService.registerPublisher(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}
}
