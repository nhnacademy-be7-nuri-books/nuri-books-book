package shop.nuribooks.books.member.authority.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityRegisterRequest;
import shop.nuribooks.books.member.authority.dto.response.AuthorityResponse;
import shop.nuribooks.books.member.authority.service.AuthorityService;

@RequiredArgsConstructor
@Controller
public class AuthorityController {

    private final AuthorityService authorityService;

    @PostMapping("/api/member/authority")
    public ResponseEntity<AuthorityResponse> authorityRegister(@RequestBody @Valid AuthorityRegisterRequest request) {
        AuthorityResponse authorityResponse = authorityService.registerAuthority(request);
        return ResponseEntity.ok(authorityResponse);
    }

    @GetMapping("/api/member/authority")
    public ResponseEntity<List<AuthorityResponse>> authorityList() {
        List<AuthorityResponse> authorities = authorityService.findAll();
        return ResponseEntity.ok(authorities);
    }

    @DeleteMapping("/api/member/authority/{authorityId}")
    public void authorityRemove(@PathVariable Long authorityId) {
        authorityService.removeAuthority(authorityId);
    }

    @PutMapping("/api/member/authority/{authorityId}")
    public ResponseEntity<AuthorityResponse> authorityModify(@PathVariable Long authorityId,
                                                             @RequestBody @Valid AuthorityEditRequest request) {
        AuthorityResponse authorityResponse = authorityService.modifyAddress(authorityId, request);
        return ResponseEntity.ok(authorityResponse);
    }
}
