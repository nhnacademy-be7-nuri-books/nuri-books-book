package shop.nuribooks.books.book.contributor.controller.role;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleRequest;
import shop.nuribooks.books.book.contributor.dto.role.ContributorRoleResponse;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.service.role.ContributorRoleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors/roles")
public class ContributorRoleController {

	private final ContributorRoleService contributorRoleService;

	@Operation(summary = "Register a new contributor role", description = "Register a new role for a contributor.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "registered successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	@PostMapping
	public ResponseEntity<ContributorRoleResponse> registerContributorRole(
		@Valid @RequestBody ContributorRoleRequest request) {
		ContributorRoleResponse response = contributorRoleService.registerContributorRole(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Get all contributor roles", description = "Retrieve all available contributor roles.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "got successfully")
	})
	@GetMapping
	public ResponseEntity<List<ContributorRole>> getContributorRole() {
		List<ContributorRole> contributorRoles = contributorRoleService.getContributorRoles();
		return ResponseEntity.status(HttpStatus.OK).body(contributorRoles);
	}

	@Operation(summary = "Delete a contributor role", description = "Remove a specific contributor role.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Contributor role not found")
	})

	@DeleteMapping("{roleName}")
	public ResponseEntity<HttpStatus> deleteContributorRole(@PathVariable String roleName) {
		contributorRoleService.deleteContributorRole(roleName);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
