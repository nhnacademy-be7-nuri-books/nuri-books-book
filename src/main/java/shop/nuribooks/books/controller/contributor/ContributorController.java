package shop.nuribooks.books.controller.contributor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributor.ContributorReq;
import shop.nuribooks.books.dto.contributor.ContributorRes;
import shop.nuribooks.books.entity.book.Contributor;
import shop.nuribooks.books.exception.contributor.InvalidContributorException;
import shop.nuribooks.books.service.contributor.ContributorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors")
public class ContributorController {

	private final ContributorService contributorService;

	@Operation(summary = "Register a new contributor",
		description = "This endpoint allows you to register a new contributor.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "registered successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request data"),
		@ApiResponse(responseCode = "500", description = "Internal server error")
	})

	@PostMapping
	public ResponseEntity<ContributorRes> registerContributor(@Valid @RequestBody ContributorReq request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			FieldError fieldError = bindingResult.getFieldError();
			String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Invalid contributor role data";
			throw new InvalidContributorException(message);
		}
		Contributor savedContributor = contributorService.registerContributor(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ContributorRes(savedContributor.getId(), savedContributor.getName()));
	}

	@Operation(summary = "Update an existing contributor",
		description = "This endpoint allows you to update the details of an existing contributor.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Contributor updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request data"),
		@ApiResponse(responseCode = "404", description = "Contributor not found"),
		@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@PutMapping("/{contributorId}")
	public ResponseEntity<ContributorRes> updateContributor(@PathVariable Long contributorId,
		@Valid @RequestBody ContributorReq request) {
		Contributor contributor = contributorService.updateContributor(contributorId, request);
		return ResponseEntity.status(HttpStatus.OK).body(new ContributorRes(contributor.getId(), contributor.getName()));
	}

	@Operation(summary = "Get a contributor by ID",
		description = "This endpoint retrieves a contributor's details by their unique ID.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Contributor found successfully")
	})
	@GetMapping("/{contributorId}")
	public ResponseEntity<ContributorRes> getContributor(
		@PathVariable Long contributorId) {
		Contributor contributor = contributorService.getContributor(contributorId);
		return ResponseEntity.status(HttpStatus.OK).body(new ContributorRes(contributor.getId(), contributor.getName()));
	}

	@Operation(summary = "Delete a contributor",
		description = "This endpoint allows you to delete an existing contributor by their ID.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Contributor deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Contributor not found"),
		@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@DeleteMapping("{contributorId}")
	public ResponseEntity<HttpStatus> deleteContributor(@PathVariable Long contributorId) {
		contributorService.deleteContributor(contributorId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
