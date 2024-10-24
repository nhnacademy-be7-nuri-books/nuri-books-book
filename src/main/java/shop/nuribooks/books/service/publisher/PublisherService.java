package shop.nuribooks.books.service.publisher;

import java.util.List;

import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;

public interface PublisherService {
	PublisherResponse registerPublisher(PublisherRequest request);

	List<PublisherResponse> getAllPublisher();

	PublisherResponse getPublisher(String name);

	void deletePublisher(String name);
}
