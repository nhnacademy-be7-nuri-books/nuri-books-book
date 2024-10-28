package shop.nuribooks.books.member.authority.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "권한 등록", description = "새로운 권한을 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "권한 타입 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/api/member/authority")
    public ResponseEntity<AuthorityResponse> authorityRegister(@RequestBody @Valid AuthorityRegisterRequest request) {
        AuthorityResponse authorityResponse = authorityService.registerAuthority(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorityResponse);
    }

    @Operation(summary = "모든 권한 조회", description = "모든 권한 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음")
    })
    @GetMapping("/api/member/authority")
    public ResponseEntity<List<AuthorityResponse>> authorityList() {
        List<AuthorityResponse> authorities = authorityService.findAll();
        return ResponseEntity.ok(authorities);
    }

    @Operation(summary = "권한 삭제", description = "ID로 권한을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음")
    })
    @DeleteMapping("/api/member/authority/{authorityId}")
    public ResponseEntity authorityRemove(@PathVariable Long authorityId) {
        authorityService.removeAuthority(authorityId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "권한 수정", description = "ID로 기존 권한을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음")
    })
    @PutMapping("/api/member/authority/{authorityId}")
    public ResponseEntity<AuthorityResponse> authorityModify(@PathVariable Long authorityId,
                                                             @RequestBody @Valid AuthorityEditRequest request) {
        AuthorityResponse authorityResponse = authorityService.modifyAddress(authorityId, request);
        return ResponseEntity.ok(authorityResponse);
    }
}
