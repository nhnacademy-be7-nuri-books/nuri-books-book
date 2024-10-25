package shop.nuribooks.books.member.authority.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.exception.authority.AuthorityAlreadyExistException;
import shop.nuribooks.books.exception.authority.AuthorityNotFoundException;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityRegisterRequest;
import shop.nuribooks.books.member.authority.dto.response.AuthorityResponse;
import shop.nuribooks.books.member.authority.entity.Authority;
import shop.nuribooks.books.member.authority.repository.AuthorityRepository;
import shop.nuribooks.books.member.authority.service.AuthorityService;

@RequiredArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public AuthorityResponse registerAuthority(AuthorityRegisterRequest request) {
        if (authorityRepository.existsByAuthorityType(request.authorityType())) {
            throw new AuthorityAlreadyExistException(request.authorityType());
        }

        Authority authority = request.toEntity();
        Authority saved = authorityRepository.save(authority);
        return AuthorityResponse.of(saved);
    }

    @Override
    public List<AuthorityResponse> findAll() {
        List<Authority> authorities = authorityRepository.findAll();
        return authorities.stream()
                .map(AuthorityResponse::of)
                .toList();
    }

    @Override
    public void removeAuthority(Long authorityId) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new AuthorityNotFoundException(authorityId));
        authorityRepository.delete(authority);
    }

    @Transactional
    @Override
    public AuthorityResponse modifyAddress(Long authorityId, AuthorityEditRequest request) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new AuthorityNotFoundException(authorityId));
        authority.editAuthority(request);
        return AuthorityResponse.of(authority);
    }
}
