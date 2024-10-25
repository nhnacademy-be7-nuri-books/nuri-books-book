package shop.nuribooks.books.service.member;

import shop.nuribooks.books.dto.member.request.CustomerRegisterRequest;
import shop.nuribooks.books.dto.member.request.CustomerUpdateRequest;
import shop.nuribooks.books.dto.member.response.CustomerRegisterResponse;
import shop.nuribooks.books.dto.member.response.CustomerUpdateResponse;

public interface CustomerService {

	/**
	 * 비회원 등록
	 */
	CustomerRegisterResponse registerCustomer(CustomerRegisterRequest request);

	/**
	 * 비회원 수정
	 */
	CustomerUpdateResponse updateCustomer(Long customerId, CustomerUpdateRequest request);
}
