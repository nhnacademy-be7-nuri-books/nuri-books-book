package shop.nuribooks.books.member.authority.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.nuribooks.books.exception.authority.AuthorityAlreadyExistException;
import shop.nuribooks.books.exception.authority.AuthorityNotFoundException;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityRegisterRequest;
import shop.nuribooks.books.member.authority.dto.response.AuthorityResponse;
import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.entity.AuthorityEnum;
import shop.nuribooks.books.member.authority.repository.AuthorityRepository;

@SpringBootTest
class AuthorityServiceImplTest {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @AfterEach
    void tearDown() {
        authorityRepository.deleteAllInBatch();
    }

    @DisplayName("권한을 등록한다.")
    @Test
    void registerAuthority() {
        // given
        AuthorityRegisterRequest authorityRegisterRequest = new AuthorityRegisterRequest(AuthorityEnum.MEMBER);

        // when
        authorityService.registerAuthority(authorityRegisterRequest);

        // then
        assertThat(authorityRepository.count()).isEqualTo(1);
    }

    @DisplayName("기존에 있는 권한 등록시 예외 발생")
    @Test
    void registerAlreadyExistAuthority() {
        // given
        Authority member = Authority.builder()
                .name(AuthorityEnum.MEMBER)
                .build();
        authorityRepository.save(member);
        AuthorityRegisterRequest authorityRegisterRequest = new AuthorityRegisterRequest(AuthorityEnum.MEMBER);

        // when // then
        assertThatThrownBy(() -> authorityService.registerAuthority(authorityRegisterRequest))
                .isInstanceOf(AuthorityAlreadyExistException.class);
    }

    @DisplayName("권한을 모두 가져온다.")
    @Test
    void findAll() {
        // given
        Authority member = Authority.builder()
                .name(AuthorityEnum.MEMBER)
                .build();

        Authority admin = Authority.builder()
                .name(AuthorityEnum.ADMIN)
                .build();

        authorityRepository.saveAll(List.of(member, admin));

        // when
        List<AuthorityResponse> all = authorityService.findAll();
        // then
        assertThat(all).hasSize(2);
    }

    @DisplayName("권한을 삭제한다.")
    @Test
    void removeAuthority() {
        // given
        Authority member = Authority.builder()
                .name(AuthorityEnum.MEMBER)
                .build();
        Authority saved = authorityRepository.save(member);

        // when
        authorityService.removeAuthority(saved.getId());

        // then
        assertThat(authorityRepository.count()).isZero();
    }

    @DisplayName("없는 권한을 삭제시 예외가 발생한다.")
    @Test
    void removeNotFoundAuthority() {
        assertThatThrownBy(() -> authorityService.removeAuthority(1L))
                .isInstanceOf(AuthorityNotFoundException.class);
    }

    @DisplayName("권한을 수정한다.")
    @Test
    void modifyAddress() {
        // given
        Authority member = Authority.builder()
                .name(AuthorityEnum.MEMBER)
                .build();
        Authority saved = authorityRepository.save(member);
        AuthorityEditRequest authorityEditRequest = new AuthorityEditRequest(saved.getId(), AuthorityEnum.ADMIN);

        // when
        AuthorityResponse changedAuthority = authorityService.modifyAddress(authorityEditRequest);

        // then
        assertThat(changedAuthority.authorityEnum()).isEqualByComparingTo(AuthorityEnum.ADMIN);
    }

    @DisplayName("없는 권한 수정시 예외가 발생한다.")
    @Test
    void modifyNotFoundAddress() {
        AuthorityEditRequest authorityEditRequest = new AuthorityEditRequest(1L, AuthorityEnum.ADMIN);
        assertThatThrownBy(() -> authorityService.modifyAddress(authorityEditRequest))
                .isInstanceOf(AuthorityNotFoundException.class);
    }
}