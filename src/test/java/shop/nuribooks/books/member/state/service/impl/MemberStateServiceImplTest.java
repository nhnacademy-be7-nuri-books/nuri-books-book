package shop.nuribooks.books.member.state.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.nuribooks.books.exception.memberstate.MemberStateAlreadyExistException;
import shop.nuribooks.books.exception.memberstate.MemberStateNotFoundException;
import shop.nuribooks.books.member.state.dto.request.MemberStateEditRequest;
import shop.nuribooks.books.member.state.dto.request.MemberStateRegisterRequest;
import shop.nuribooks.books.member.state.dto.response.MemberStateResponse;
import shop.nuribooks.books.member.state.entity.MemberState;
import shop.nuribooks.books.member.state.entity.MemberStateType;
import shop.nuribooks.books.member.state.repository.MemberStateRepository;
import shop.nuribooks.books.member.state.service.MemberStateService;

@SpringBootTest
class MemberStateServiceImplTest {

    @Autowired
    private MemberStateService memberStateService;

    @Autowired
    private MemberStateRepository memberStateRepository;

    @AfterEach
    void tearDown() {
        memberStateRepository.deleteAllInBatch();
    }

    @DisplayName("회원 상태를 등록한다")
    @Test
    void registerMemberState() {
        // given
        MemberStateRegisterRequest memberStateRegisterRequest = new MemberStateRegisterRequest(MemberStateType.ACTIVE);

        // when
        memberStateService.registerMemberState(memberStateRegisterRequest);

        // then
        assertThat(memberStateRepository.count()).isEqualTo(1);
    }

    @DisplayName("기존의 있는 회원 상태 등록시 예외 발생")
    @Test
    void registerAlreadyExistMemberState() {
        // given
        MemberState active = MemberState.builder()
                .memberStateType(MemberStateType.ACTIVE)
                .build();
        memberStateRepository.save(active);
        MemberStateRegisterRequest memberStateRegisterRequest = new MemberStateRegisterRequest(MemberStateType.ACTIVE);
        // when
        assertThatThrownBy(() -> memberStateService.registerMemberState(memberStateRegisterRequest))
                .isInstanceOf(MemberStateAlreadyExistException.class);

        // then
    }

    @DisplayName("회원 상태를 모두 가져온다")
    @Test
    void findAll() {
        // given
        MemberState active = MemberState.builder()
                .memberStateType(MemberStateType.ACTIVE)
                .build();

        MemberState dormant = MemberState.builder()
                .memberStateType(MemberStateType.DORMANT)
                .build();

        memberStateRepository.saveAll(List.of(active, dormant));

        // when
        List<MemberStateResponse> all = memberStateService.findAll();

        // then
        assertThat(all).hasSize(2);
    }

    @DisplayName("회원 상태를 삭제한다.")
    @Test
    void removeMemberState() {
        // given
        MemberState active = MemberState.builder()
                .memberStateType(MemberStateType.ACTIVE)
                .build();
        MemberState saved = memberStateRepository.save(active);

        // when
        memberStateService.removeMemberState(saved.getId());

        // then
        assertThat(memberStateRepository.count()).isZero();
    }

    @DisplayName("없는 회원 상태 삭제시 예외가 발생한다.")
    @Test
    void removeNotFoundMemberState() {
        assertThatThrownBy(() -> memberStateService.removeMemberState(1L))
                .isInstanceOf(MemberStateNotFoundException.class);
    }

    @DisplayName("회원 상태를 수정한다.")
    @Test
    void modifyMemberState() {
        // given
        MemberState active = MemberState.builder()
                .memberStateType(MemberStateType.ACTIVE)
                .build();
        MemberState saved = memberStateRepository.save(active);
        MemberStateEditRequest memberStateEditRequest = new MemberStateEditRequest(MemberStateType.DORMANT);

        // when
        MemberStateResponse changedMemberState = memberStateService.modifyMemberState(saved.getId(),
                memberStateEditRequest);

        // then
        assertThat(changedMemberState.memberStateType()).isEqualByComparingTo(MemberStateType.DORMANT);
    }

    @DisplayName("없는 회원 상태 수정시 예외가 발생한다.")
    @Test
    void modifyNotFoundMemberState() {
        MemberStateEditRequest memberStateEditRequest = new MemberStateEditRequest(MemberStateType.ACTIVE);
        assertThatThrownBy(() -> memberStateService.modifyMemberState(1L, memberStateEditRequest))
                .isInstanceOf(MemberStateNotFoundException.class);
    }
}