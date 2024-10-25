package shop.nuribooks.books.member.member.service;

import shop.nuribooks.books.member.member.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.CustomerUpdateRequest;
import shop.nuribooks.books.member.member.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.CustomerUpdateResponse;

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
