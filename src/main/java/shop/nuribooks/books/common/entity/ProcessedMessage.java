package shop.nuribooks.books.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_messages")
@NoArgsConstructor
@Getter
public class ProcessedMessage {
	@Id
	private String messageId;

	private LocalDateTime processedAt;

	@Builder
	public ProcessedMessage(String messageId) {
		this.messageId = messageId;
		this.processedAt = LocalDateTime.now();
	}
}
