package shop.nuribooks.books.cart.customer.service;

import java.util.List;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;

public interface CustomerCartService {

    void addToCart(CustomerCartAddRequest request);

//    List<CustomerCartResponse> getCustomerCartList(Long sessionId);
}
