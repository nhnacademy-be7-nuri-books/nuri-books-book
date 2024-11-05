package shop.nuribooks.books.book.publisher.service;

import java.util.List;

import shop.nuribooks.books.book.publisher.dto.PublisherRequest;
import shop.nuribooks.books.book.publisher.dto.PublisherResponse;

public interface PublisherService {

	PublisherResponse registerPublisher(PublisherRequest request);

	List<PublisherResponse> getAllPublisher();

	PublisherResponse getPublisher(Long id);

	void deletePublisher(Long id);

	PublisherResponse updatePublisher(Long id, PublisherRequest request);
}
