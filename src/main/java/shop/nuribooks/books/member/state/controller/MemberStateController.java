package shop.nuribooks.books.member.state.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/api/member/state")
    public ResponseEntity<MemberStateResponse> memberStateRegister(
            @RequestBody @Valid MemberStateRegisterRequest request) {
        MemberStateResponse memberStateResponse = memberStateService.registerMemberState(request);
        return ResponseEntity.ok(memberStateResponse);
    }

    @GetMapping("/api/member/state")
    public ResponseEntity<List<MemberStateResponse>> memberStateList() {
        List<MemberStateResponse> memberStates = memberStateService.findAll();
        return ResponseEntity.ok(memberStates);
    }

    @DeleteMapping("/api/member/state/{memberStateId}")
    public void memberStateRemove(@PathVariable Long memberStateId) {
        memberStateService.removeMemberState(memberStateId);
    }

    @PutMapping("/api/member/state/{memberStateId}")
    public ResponseEntity<MemberStateResponse> memberStateModify(@PathVariable Long memberStateId,
                                                                 @RequestBody @Valid MemberStateEditRequest request) {
        MemberStateResponse memberStateResponse = memberStateService.modifyMemberState(memberStateId, request);
        return ResponseEntity.ok(memberStateResponse);
    }
}
