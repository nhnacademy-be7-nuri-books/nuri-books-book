package shop.nuribooks.books.cart.entity;

public enum RedisCartKey {
	MEMBER_CART("member:"),
	CUSTOMER_KEY("customer:"),
	SHADOW_KEY("expire:timer:");

	private final String key;

	RedisCartKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String withSuffix(String suffix) {
		return key + suffix;
	}
}
