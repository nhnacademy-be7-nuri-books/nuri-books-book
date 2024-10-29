package shop.nuribooks.books.member.state.service;

import java.util.List;
import shop.nuribooks.books.member.state.dto.request.MemberStateEditRequest;
import shop.nuribooks.books.member.state.dto.request.MemberStateRegisterRequest;
import shop.nuribooks.books.member.state.dto.response.MemberStateResponse;

public interface MemberStateService {

    MemberStateResponse registerMemberState(MemberStateRegisterRequest request);

    List<MemberStateResponse> findAll();

    void removeMemberState(Long memberStateId);

    MemberStateResponse modifyMemberState(Long memberStateId, MemberStateEditRequest request);
}
