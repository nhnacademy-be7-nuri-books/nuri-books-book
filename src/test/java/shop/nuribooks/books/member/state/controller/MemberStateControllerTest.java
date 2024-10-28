package shop.nuribooks.books.member.state.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import shop.nuribooks.books.member.state.dto.request.MemberStateEditRequest;
import shop.nuribooks.books.member.state.dto.request.MemberStateRegisterRequest;
import shop.nuribooks.books.member.state.entity.MemberStateType;
import shop.nuribooks.books.member.state.service.MemberStateService;

@WebMvcTest(MemberStateController.class)
class MemberStateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberStateService memberStateService;

    @DisplayName("회원 상태를 등록한다.")
    @Test
    void memberStateRegister() throws Exception {
        // given
        MemberStateRegisterRequest memberStateRegisterRequest = new MemberStateRegisterRequest(MemberStateType.ACTIVE);

        // when
        mockMvc.perform(post("/api/member/state")
                        .content(objectMapper.writeValueAsString(memberStateRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("enum 역직렬화에 실패할 경우 예외가 던져진다.")
    @Test
    void memberStateRegisterFailDeserialize() throws Exception {
        // given

        // when
        mockMvc.perform(post("/api/member/state")
                        .content("{\"memberStateType\":\"aactive\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    Throwable thrown = result.getResolvedException();
                    assertThat(thrown).isInstanceOf(MethodArgumentNotValidException.class);
                });

        // then
    }

    @DisplayName("회원 상태를 가져온다")
    @Test
    void memberStateList() throws Exception {
        // given
        when(memberStateService.findAll()).thenReturn(List.of());
        // when
        mockMvc.perform(get("/api/member/state"))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원 상태를 삭제한다.")
    @Test
    void memberStateRemove() throws Exception {
        // given

        // when
        mockMvc.perform(delete("/api/member/state/{memberStateId}", 1L))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원 상태를 수정한다.")
    @Test
    void memberStateModify() throws Exception {
        // given
        MemberStateEditRequest memberStateEditRequest = new MemberStateEditRequest(MemberStateType.ACTIVE);

        // when // then
        mockMvc.perform(put("/api/member/state/{memberStateId}", 1L)
                        .content(objectMapper.writeValueAsString(memberStateEditRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}