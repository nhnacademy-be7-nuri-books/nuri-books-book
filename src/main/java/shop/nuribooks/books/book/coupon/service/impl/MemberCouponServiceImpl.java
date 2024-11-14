package shop.nuribooks.books.book.coupon.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberCouponServiceImpl implements MemberCouponService {
}
