package shop.nuribooks.books.controller.contributorrole;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleRes;
import shop.nuribooks.books.entity.book.ContributorRole;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;
import shop.nuribooks.books.service.contributorrole.ContributorRoleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors/roles")
public class ContributorRoleController {

	private final ContributorRoleService contributorRoleService;

	@PostMapping
	public ResponseEntity<ContributorRoleRes> registerContributorRole(
		@Valid @RequestBody ContributorRoleReq request, BindingResult bindingResult) {

		if(bindingResult.hasErrors()) {
			FieldError fieldError = bindingResult.getFieldError();
			String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Invalid contributor role data";
			throw new InvalidContributorRoleException(message);
		}

		contributorRoleService.registerContributorRole(request);
		return ResponseEntity.status(HttpStatus.OK).body(new ContributorRoleRes(request.getName()));
	}

	@GetMapping
	public ResponseEntity<List<ContributorRole>> getContributorRole() {
		List<ContributorRole> contributorRoles = contributorRoleService.getContributorRoles();
		return ResponseEntity.status(HttpStatus.OK).body(contributorRoles);
	}

	@PutMapping("/{roleName}")
	public ResponseEntity<ContributorRoleRes> updateContributorRole(@PathVariable String roleName,
		@Valid @RequestBody ContributorRoleReq request, BindingResult bindingResult) {

		if(bindingResult.hasErrors()) {
			FieldError fieldError = bindingResult.getFieldError();
			String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Invalid contributor role data";
			throw new InvalidContributorRoleException(message);
		}

		contributorRoleService.updateContributorRole(roleName, request);
		return ResponseEntity.status(HttpStatus.OK).body(new ContributorRoleRes(request.getName()));
	}

	@DeleteMapping("{roleName}")
	public ResponseEntity<HttpStatus> deleteContributorRole(@PathVariable String roleName) {
		contributorRoleService.deleteContributorRole(roleName);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
