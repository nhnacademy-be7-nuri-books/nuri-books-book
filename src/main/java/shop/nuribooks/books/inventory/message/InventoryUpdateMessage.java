package shop.nuribooks.books.inventory.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventoryUpdateMessage {
	private Long bookId;
	private int count;
	private String messageId; //멱등성을 위한 고유 ID 추
}
