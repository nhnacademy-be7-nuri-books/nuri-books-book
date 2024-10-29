package shop.nuribooks.books.member.state.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.member.state.dto.request.MemberStateEditRequest;
import shop.nuribooks.books.member.state.dto.request.MemberStateRegisterRequest;
import shop.nuribooks.books.member.state.dto.response.MemberStateResponse;
import shop.nuribooks.books.member.state.service.MemberStateService;

@RequiredArgsConstructor
@RestController
public class MemberStateController {
    private final MemberStateService memberStateService;

    /**
     * 새로운 회원 상태를 등록합니다.
     *
     * @param request 회원 상태 등록 요청 데이터
     * @return 등록된 회원 상태의 응답
     */
    @Operation(summary = "회원 상태 등록", description = "새로운 회원 상태를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 상태 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/api/member/state")
    public ResponseEntity<MemberStateResponse> memberStateRegister(
            @RequestBody @Valid MemberStateRegisterRequest request) {
        MemberStateResponse memberStateResponse = memberStateService.registerMemberState(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberStateResponse);
    }

    /**
     * 모든 회원 상태 목록을 조회합니다.
     *
     * @return 모든 회원 상태의 리스트
     */
    @Operation(summary = "회원 상태 목록 조회", description = "모든 회원 상태 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 상태 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원 상태를 찾을 수 없음")
    })
    @GetMapping("/api/member/state")
    public ResponseEntity<List<MemberStateResponse>> memberStateList() {
        List<MemberStateResponse> memberStates = memberStateService.findAll();
        return ResponseEntity.ok(memberStates);
    }

    /**
     * 지정된 ID의 회원 상태를 삭제합니다.
     *
     * @param memberStateId 삭제할 회원 상태의 ID
     * @return HTTP 204 No Content
     */
    @Operation(summary = "회원 상태 삭제", description = "지정된 ID의 회원 상태를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 상태 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "회원 상태를 찾을 수 없음")
    })
    @DeleteMapping("/api/member/state/{memberStateId}")
    public ResponseEntity memberStateRemove(@PathVariable Long memberStateId) {
        memberStateService.removeMemberState(memberStateId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 지정된 ID의 회원 상태를 수정합니다.
     *
     * @param memberStateId 수정할 회원 상태의 ID
     * @param request 회원 상태 수정 요청 데이터
     * @return 수정된 회원 상태의 응답
     */
    @Operation(summary = "회원 상태 수정", description = "지정된 ID의 회원 상태를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 상태 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원 상태를 찾을 수 없음")
    })
    @PutMapping("/api/member/state/{memberStateId}")
    public ResponseEntity<MemberStateResponse> memberStateModify(@PathVariable Long memberStateId,
                                                                 @RequestBody @Valid MemberStateEditRequest request) {
        MemberStateResponse memberStateResponse = memberStateService.modifyMemberState(memberStateId, request);
        return ResponseEntity.ok(memberStateResponse);
    }
}
