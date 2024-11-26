package shop.nuribooks.books.inventory.listener;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.inventory.service.InventoryService;

@Component
@RequiredArgsConstructor
public class InventoryUpdateListener {
	private final InventoryService inventoryService;
}
