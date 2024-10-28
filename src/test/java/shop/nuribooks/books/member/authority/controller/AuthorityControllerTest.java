package shop.nuribooks.books.member.authority.controller;

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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityRegisterRequest;
import shop.nuribooks.books.member.authority.entity.AuthorityType;
import shop.nuribooks.books.member.authority.service.AuthorityService;

@WebMvcTest(AuthorityController.class)
class AuthorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorityService authorityService;

    @DisplayName("권한을 등록한다.")
    @Test
    void authorityRegister() throws Exception {
        // given
        AuthorityRegisterRequest authorityRegisterRequest = new AuthorityRegisterRequest(AuthorityType.MEMBER);

        // when // then
        mockMvc.perform(post("/api/member/authority")
                        .content(objectMapper.writeValueAsString(authorityRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @DisplayName("enum 역직렬화에 실패할 경우 HttpMessageNotReadableException 예외가 던져진다.")
    @Test
    void authorityRegisterFailDeserialize() throws Exception {
        // given

        // when // then
        mockMvc.perform(post("/api/member/authority")
                        .content("{\"authorityType\":\"MaaaaEMBER\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    Throwable thrown = result.getResolvedException();
                    assertThat(thrown).isInstanceOf(MethodArgumentNotValidException.class); // 예외 타입 확인
                });

    }

    @DisplayName("권한들을 가져온다")
    @Test
    void authorityList() throws Exception {
        // given
        when(authorityService.findAll()).thenReturn(List.of());

        // when // then
        mockMvc.perform(get("/api/member/authority"))
                .andExpect(status().isOk());
    }

    @DisplayName("권한을 삭제한다.")
    @Test
    void authorityRemove() throws Exception {
        // given
        // when // then
        mockMvc.perform(delete("/api/member/authority/{authorityId}", 1L))
                .andExpect(status().isOk());
    }

    @DisplayName("권한을 수정한다.")
    @Test
    void authorityModify() throws Exception {
        // given
        AuthorityEditRequest authorityEditRequest = new AuthorityEditRequest(AuthorityType.MEMBER);

        // when // then
        mockMvc.perform(put("/api/member/authority/{authorityId}", 1L)
                        .content(objectMapper.writeValueAsString(authorityEditRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}