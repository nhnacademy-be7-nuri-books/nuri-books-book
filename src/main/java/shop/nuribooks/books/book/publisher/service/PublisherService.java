package shop.nuribooks.books.book.publisher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.publisher.dto.PublisherRequest;
import shop.nuribooks.books.book.publisher.dto.PublisherResponse;

public interface PublisherService {

	PublisherResponse registerPublisher(PublisherRequest request);

	Page<PublisherResponse> getAllPublisher(Pageable pageable);

	PublisherResponse getPublisher(Long id);

	void deletePublisher(Long id);

	PublisherResponse updatePublisher(Long id, PublisherRequest request);
}
