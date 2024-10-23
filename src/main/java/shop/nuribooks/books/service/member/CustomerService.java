package shop.nuribooks.books.service.member;

import shop.nuribooks.books.dto.member.request.CustomerRegisterRequest;
import shop.nuribooks.books.dto.member.response.CustomerRegisterResponse;

public interface CustomerService {

	/**
	 * 비회원 저장
	 */
	CustomerRegisterResponse createCustomer(CustomerRegisterRequest request);
}
