package shop.nuribooks.books.member.customer.service;

import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.request.CustomerUpdateRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.dto.response.CustomerUpdateResponse;

/**
 * @author Jprotection
 */
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
