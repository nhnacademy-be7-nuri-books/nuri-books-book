package shop.nuribooks.books.member.authority.service;

import java.util.List;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityRegisterRequest;
import shop.nuribooks.books.member.authority.dto.response.AuthorityResponse;

public interface AuthorityService {

    AuthorityResponse registerAuthority(AuthorityRegisterRequest request);

    List<AuthorityResponse> findAll();

    void removeAuthority(Long authorityId);

    AuthorityResponse modifyAddress(AuthorityEditRequest request);
}
