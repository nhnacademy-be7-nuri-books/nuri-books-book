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
import shop.nuribooks.books.entity.Contributors;
import shop.nuribooks.books.service.contributor.ContributorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors")
public class ContributorController {

	private final ContributorService contributorService;

	@PostMapping
	public ResponseEntity<ContributorRes> registerContributor(@Valid @RequestBody ContributorReq request) {
		Contributors savedContributor = contributorService.registerContributor(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ContributorRes(savedContributor.getId(), savedContributor.getName()));	}

}
