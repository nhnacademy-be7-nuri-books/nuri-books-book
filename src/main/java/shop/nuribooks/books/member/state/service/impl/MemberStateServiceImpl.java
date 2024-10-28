package shop.nuribooks.books.member.state.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.exception.memberstate.MemberStateAlreadyExistException;
import shop.nuribooks.books.exception.memberstate.MemberStateNotFoundException;
import shop.nuribooks.books.member.state.dto.request.MemberStateEditRequest;
import shop.nuribooks.books.member.state.dto.request.MemberStateRegisterRequest;
import shop.nuribooks.books.member.state.dto.response.MemberStateResponse;
import shop.nuribooks.books.member.state.entity.MemberState;
import shop.nuribooks.books.member.state.repository.MemberStateRepository;
import shop.nuribooks.books.member.state.service.MemberStateService;

@RequiredArgsConstructor
@Service
public class MemberStateServiceImpl implements MemberStateService {

    private final MemberStateRepository memberStateRepository;

    @Override
    public MemberStateResponse registerMemberState(MemberStateRegisterRequest request) {
        if (memberStateRepository.existsByMemberStateType(request.memberStateType())) {
            throw new MemberStateAlreadyExistException(request.memberStateType());
        }

        MemberState memberState = request.toEntity();
        MemberState saved = memberStateRepository.save(memberState);
        return MemberStateResponse.of(saved);
    }

    @Override
    public List<MemberStateResponse> findAll() {
        List<MemberState> memberStates = memberStateRepository.findAll();
        return memberStates.stream()
                .map(MemberStateResponse::of)
                .toList();
    }

    @Override
    public void removeMemberState(Long memberStateId) {
        MemberState memberState = memberStateRepository.findById(memberStateId)
                .orElseThrow(() -> new MemberStateNotFoundException(memberStateId));
        memberStateRepository.delete(memberState);
    }

    @Transactional
    @Override
    public MemberStateResponse modifyMemberState(Long memberStateId, MemberStateEditRequest request) {
        MemberState memberState = memberStateRepository.findById(memberStateId)
                .orElseThrow(() -> new MemberStateNotFoundException(memberStateId));
        memberState.editMemberState(request);
        return MemberStateResponse.of(memberState);
    }
}
