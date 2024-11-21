package shop.nuribooks.books.member.customer.service;

import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;

/**
 * @author Jprotection
 */
public interface CustomerService {

	CustomerRegisterResponse registerCustomer(CustomerRegisterRequest request);
}
