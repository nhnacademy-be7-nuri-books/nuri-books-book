package shop.nuribooks.books.inventory.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.config.rabbitmq.RabbitmqConfig;
import shop.nuribooks.books.common.entity.ProcessedMessage;
import shop.nuribooks.books.common.repository.ProcessedMessageRepository;
import shop.nuribooks.books.inventory.message.InventoryUpdateMessage;
import shop.nuribooks.books.inventory.service.InventoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryUpdateListener {
	private final InventoryService inventoryService;
	private final ProcessedMessageRepository processedMessageRepository;

	@Transactional
	@RabbitListener(queues = RabbitmqConfig.INVENTORY_UPDATE_KEY, concurrency = "1")
	public void handleInventoryUpdate(InventoryUpdateMessage message) {
		log.info("Received inventory update message: {}", message);

		//메시지 유효성 검증
		if (message.getBookId() == null || message.getCount() <= 0) {
			//메시지를 DLQ로 보내기 위한 예외 (재큐잉 될 수 없도록)
			throw new AmqpRejectAndDontRequeueException("잘못된 재고 업데이트 요청 입니다.");
		}

		// idempotency 체크
		if (processedMessageRepository.existsById(message.getMessageId())) {
			log.warn("Received a message with id {} which is already processed", message.getMessageId());
			return;
		}

		try {
			//재고 업데이트 로직 호출
			inventoryService.updateStock(message.getBookId(), message.getCount());

			//메시지 처리 완료
			ProcessedMessage processedMessage = ProcessedMessage.builder()
				.messageId(message.getMessageId())
				.build();

			processedMessageRepository.save(processedMessage);

			log.info("Processed message with id {} successfully", message.getMessageId());
		} catch (Exception e) {
			log.error("Failed to update stock", e);
			//DLQ로 이동
			throw new AmqpRejectAndDontRequeueException("Failed to update stock", e);
		}
	}
}
