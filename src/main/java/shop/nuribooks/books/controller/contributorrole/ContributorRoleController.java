package shop.nuribooks.books.controller.contributorrole;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleReq;
import shop.nuribooks.books.dto.contributorrole.ContributorRoleRes;
import shop.nuribooks.books.entity.ContributorRoles;
import shop.nuribooks.books.service.contributorrole.ContributorRoleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors/roles")
public class ContributorRoleController {

	private final ContributorRoleService contributorRoleService;

	@PostMapping
	public ResponseEntity<ContributorRoleRes> registerContributorRole(
		@Valid @RequestBody ContributorRoleReq request) {
		contributorRoleService.registerContributorRole(request);
		return ResponseEntity.status(HttpStatus.OK).body(new ContributorRoleRes(request.getName()));
	}

	@GetMapping
	public ResponseEntity<List<ContributorRoles>> getContributorRole() {
		List<ContributorRoles> contributorRoles = contributorRoleService.getContributorRoles();
		return ResponseEntity.status(HttpStatus.OK).body(contributorRoles);
	}
}
