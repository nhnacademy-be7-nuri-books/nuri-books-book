package shop.nuribooks.books.controller.publisher;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	/**
	 * 모든 출판사 정보 조회하는 controller
	 * @author kyongmin
	 *
	 * @return 모든 출판사 목록이 담긴 ResponseEntity
	 */
	@Operation(summary = "모든 출판사 정보 조회", description = "모든 출판사의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "출판사 목록 조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/api/publishers")
	public ResponseEntity<List<PublisherResponse>> getAllPublisher() {
		List<PublisherResponse> publisherResponses = publisherService.getAllPublisher();
		return ResponseEntity.status(HttpStatus.OK).body(publisherResponses);
	}

	/**
	 * 요청받은 이름에 해당하는 출판사 정보 조회하는 controller
	 * @author kyongmin
	 *
	 * @param publisherName 조회할 출판사 이름
	 * @return 해당 이름의 출판사 정보가 담긴 ResponseEntity
	 */
	@Operation(summary = "특정 출판사 정보 조회", description = "출판사 이름을 받아 해당 출판사의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "출판사 조회 성공"),
		@ApiResponse(responseCode = "404", description = "출판사 미존재"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@GetMapping("/api/publishers/{publisherName}")
	public ResponseEntity<PublisherResponse> getPublisher(@Valid @PathVariable String publisherName) {
		PublisherResponse response = publisherService.getPublisher(publisherName);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}
}
