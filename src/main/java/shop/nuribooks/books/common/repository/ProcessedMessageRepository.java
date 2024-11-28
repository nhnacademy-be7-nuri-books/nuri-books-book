package shop.nuribooks.books.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.common.entity.ProcessedMessage;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}
