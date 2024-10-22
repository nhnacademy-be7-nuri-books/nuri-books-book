package shop.nuribooks.books.controller.contributor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributor.ContributorReq;
import shop.nuribooks.books.dto.contributor.ContributorRes;
import shop.nuribooks.books.service.contributor.ContributorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContributorController {

	private final ContributorService contributorService;

	@PostMapping("/contributors")
	public ResponseEntity<ContributorRes> registerContributor(
		@Valid @RequestBody ContributorReq request) {
		contributorService.registerContributor(request);
		return ResponseEntity.status(HttpStatus.OK).body(new ContributorRes(request.getName()));
	}
}
