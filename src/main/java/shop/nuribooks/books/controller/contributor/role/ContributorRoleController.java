package shop.nuribooks.books.controller.contributor.role;

import java.util.List;

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
import shop.nuribooks.books.dto.contributor.role.ContributorRoleRequest;
import shop.nuribooks.books.dto.contributor.role.ContributorRoleResponse;
import shop.nuribooks.books.entity.book.ContributorRole;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;
import shop.nuribooks.books.service.contributor.role.ContributorRoleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors/roles")
public class ContributorRoleController {

	private final ContributorRoleService contributorRoleService;

	@Operation(summary = "Register a new contributor role", description = "Register a new role for a contributor.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "registered successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	@PostMapping
	public ResponseEntity<ContributorRoleResponse> registerContributorRole(
		@Valid @RequestBody ContributorRoleRequest request, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			FieldError fieldError = bindingResult.getFieldError();
			String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Invalid contributor role data";
			throw new InvalidContributorRoleException(message);
		}

		contributorRoleService.registerContributorRole(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ContributorRoleResponse(request.getName()));
	}

	@Operation(summary = "Get all contributor roles", description = "Retrieve all available contributor roles.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "got successfully")
	})
	@GetMapping
	public ResponseEntity<List<ContributorRole>> getContributorRole() {
		List<ContributorRole> contributorRoles = contributorRoleService.getContributorRoles();
		return ResponseEntity.status(HttpStatus.OK).body(contributorRoles);
	}

	@Operation(summary = "Update a contributor role", description = "Update the name of a specific contributor role.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request data"),
		@ApiResponse(responseCode = "404", description = "Contributor role not found")
	})
	@PutMapping("/{roleName}")
	public ResponseEntity<ContributorRoleResponse> updateContributorRole(@PathVariable String roleName,
		@Valid @RequestBody ContributorRoleRequest request, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			FieldError fieldError = bindingResult.getFieldError();
			String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Invalid contributor role data";
			throw new InvalidContributorRoleException(message);
		}

		contributorRoleService.updateContributorRole(roleName, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ContributorRoleResponse(request.getName()));
	}

	@Operation(summary = "Delete a contributor role", description = "Remove a specific contributor role.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Contributor role not found")
	})

	@DeleteMapping("{roleName}")
	public ResponseEntity<HttpStatus> deleteContributorRole(@PathVariable String roleName) {
		contributorRoleService.deleteContributorRole(roleName);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
