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

    /**
     * 새로운 회원 상태를 등록합니다.
     *
     * @param request 회원 상태 등록 요청 데이터
     * @return 등록된 회원 상태의 응답
     * @throws MemberStateAlreadyExistException 이미 존재하는 회원 상태 타입일 경우
     */
    @Override
    public MemberStateResponse registerMemberState(MemberStateRegisterRequest request) {
        if (memberStateRepository.existsByMemberStateType(request.memberStateType())) {
            throw new MemberStateAlreadyExistException(request.memberStateType());
        }

        MemberState memberState = request.toEntity();
        MemberState saved = memberStateRepository.save(memberState);
        return MemberStateResponse.of(saved);
    }

    /**
     * 모든 회원 상태를 조회합니다.
     *
     * @return 모든 회원 상태의 리스트
     */
    @Override
    public List<MemberStateResponse> findAll() {
        List<MemberState> memberStates = memberStateRepository.findAll();
        return memberStates.stream()
                .map(MemberStateResponse::of)
                .toList();
    }

    /**
     * 지정된 ID의 회원 상태를 삭제합니다.
     *
     * @param memberStateId 삭제할 회원 상태의 ID
     * @throws MemberStateNotFoundException 회원 상태를 찾을 수 없는 경우
     */
    @Override
    public void removeMemberState(Long memberStateId) {
        MemberState memberState = memberStateRepository.findById(memberStateId)
                .orElseThrow(() -> new MemberStateNotFoundException(memberStateId));
        memberStateRepository.delete(memberState);
    }

    /**
     * 지정된 ID의 회원 상태를 수정합니다.
     *
     * @param memberStateId 수정할 회원 상태의 ID
     * @param request 회원 상태 수정 요청 데이터
     * @return 수정된 회원 상태의 응답
     * @throws MemberStateNotFoundException 회원 상태를 찾을 수 없는 경우
     */
    @Transactional
    @Override
    public MemberStateResponse modifyMemberState(Long memberStateId, MemberStateEditRequest request) {
        MemberState memberState = memberStateRepository.findById(memberStateId)
                .orElseThrow(() -> new MemberStateNotFoundException(memberStateId));
        memberState.editMemberState(request);
        return MemberStateResponse.of(memberState);
    }
}
