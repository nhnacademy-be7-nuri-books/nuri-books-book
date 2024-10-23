package shop.nuribooks.books.service.member;

import shop.nuribooks.books.dto.member.request.CustomerCreateRequest;
import shop.nuribooks.books.dto.member.response.CustomerCreateResponse;

public interface CustomerService {

	/**
	 * 비회원 저장
	 */
	CustomerCreateResponse createCustomer(CustomerCreateRequest request);
}
