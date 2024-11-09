package shop.nuribooks.books.book.point.service;

import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.entity.PointPolicy;

public interface PointPolicyService {
	PointPolicy registerPointPolicy(PointPolicyRequest pointPolicyRequest);
	
}
