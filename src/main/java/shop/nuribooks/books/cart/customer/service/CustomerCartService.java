package shop.nuribooks.books.cart.customer.service;

import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;

public interface CustomerCartService {

    void addToCart(CustomerCartAddRequest request);

}
