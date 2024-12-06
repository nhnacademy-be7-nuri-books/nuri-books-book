package shop.nuribooks.books.book.coupon.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shop.nuribooks.books.book.coupon.enums.CouponType;

@Component
public class CouponStrategyFactory {

	private final Map<CouponType, CouponStrategy> strategies = new HashMap<>();

	@Autowired
	public CouponStrategyFactory(List<CouponStrategy> strategyList) {
		for (CouponStrategy strategy : strategyList) {
			if (strategy instanceof BookCouponStrategy) {
				strategies.put(CouponType.BOOK, strategy);
			} else if (strategy instanceof CategoryCouponStrategy) {
				strategies.put(CouponType.CATEGORY, strategy);
			} else if (strategy instanceof AllCouponStrategy) {
				strategies.put(CouponType.ALL, strategy);
			}
			// 새로운 타입 추가 시 여기에 맵핑 추가 가능
		}
	}

	public CouponStrategy getStrategy(CouponType type) {
		return Optional.ofNullable(strategies.get(type))
			.orElseThrow(() -> new IllegalArgumentException("Unsupported coupon type: " + type));
	}
}
