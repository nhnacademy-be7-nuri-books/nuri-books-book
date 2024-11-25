package shop.nuribooks.books.order.returnrequest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.returnrequest.service.ReturnRequestService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/return-requests")
public class ReturnRequestController {
	private final ReturnRequestService returnRequestService;

}

