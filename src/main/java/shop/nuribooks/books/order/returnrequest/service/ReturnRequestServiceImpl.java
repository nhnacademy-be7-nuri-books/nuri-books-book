package shop.nuribooks.books.order.returnrequest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReturnRequestServiceImpl implements ReturnRequestService {

}
