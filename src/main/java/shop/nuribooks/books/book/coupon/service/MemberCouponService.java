package shop.nuribooks.books.book.coupon.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.MemberCouponIssueRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.message.BookCouponIssueMessage;

public interface MemberCouponService {

	void registerMemberCoupon(MemberCouponIssueRequest memberCouponIssueRequest);

	List<MemberCouponResponse> getAllCouponByMemberId(Long memberId);

	void updateIsUsed(Long memberCouponId);

	void deleteMemberCoupon(Long memberCouponId);

	Page<MemberCouponResponse> getAvailableCouponsByMemberId(Long memberId, Pageable pageable);

	Page<MemberCouponResponse> getExpiredOrUsedCouponsByMemberId(Long memberId, Pageable pageable);

	List<MemberCouponOrderDto> getAllTypeAvailableCouponsByMemberId(Long memberId, BigDecimal orderTotalPrice);

	MemberCouponOrderDto getMemberCoupon(Long memberCouponId);

	void issueBookCoupon(BookCouponIssueMessage message);

	void publishBookCouponIssue(Long memberId, Long couponId);
}
