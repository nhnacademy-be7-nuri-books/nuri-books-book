package shop.nuribooks.books.payment.payment.entity;

/**
 * 결제 상태 Enum
 * <ul>
 *     <li><b>PENDING</b>: 결제 대기 상태 (사용자가 결제를 시작했으나 아직 완료되지 않음)</li>
 *     <li><b>COMPLETED</b>: 결제 완료 상태 (결제가 성공적으로 이루어진 상태)</li>
 *     <li><b>CANCELED</b>: 결제 취소 상태 (결제가 취소된 상태)</li>
 * </ul>
 *
 * @author nuri
 */
public enum PaymentMethod {
	CARD(0, "카드"),
	EASY_PAY(1, "간편결제"),
	VIRTUAL_ACCOUNT(2, "가상계좌"),
	MOBILE_PHONE(3, "휴대폰"),
	TRANSFER(4, "계좌이체"),
	CULTURE_GIFT_CERTIFICATE(5, "문화상품권"),
	BOOK_GIFT_CERTIFICATE(6, "도서문화상품권"),
	GAME_GIFT_CERTIFICATE(7, "게임문화상품권");


	private final int code;
	private final String korName;

	/**
	 * 결제 상태 생성자
	 * @param code 결제 상태 코드
	 * @param korName 결제 상태의 한국어 이름
	 */
	PaymentMethod(int code, String korName) {
		this.code = code;
		this.korName = korName;
	}

	/**
	 * 결제 상태의 코드 값 반환
	 * @return 결제 상태의 코드
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 결제 상태의 한국어 이름을 반환
	 * @return 결제 상태의 한국어 이름
	 */
	public String getKorName() {return korName;}

	public static PaymentMethod fromKoreanName(String korName) {
		for (PaymentMethod method : PaymentMethod.values()) {
			if (method.korName.equalsIgnoreCase(korName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Unknown Korean name: " + korName);
	}
}
