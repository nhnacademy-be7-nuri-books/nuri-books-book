package shop.nuribooks.books.inventory.message;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class InventoryUpdateMessage {
	private Long bookId;
	private int count;
	private String messageId; //멱등성을 위한 고유 ID

	@Builder
	public InventoryUpdateMessage(Long bookId, int count, String messageId) {
		this.bookId = bookId;
		this.count = count;
		this.messageId = messageId;
	}
}
