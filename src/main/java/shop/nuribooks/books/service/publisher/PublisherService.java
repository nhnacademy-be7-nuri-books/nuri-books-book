package shop.nuribooks.books.service.publisher;

import java.util.List;

import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;

public interface PublisherService {
	//TODO: 수정, 삭제 구현
	PublisherResponse registerPublisher(PublisherRequest request);

	List<PublisherResponse> getAllPublisher();

	PublisherResponse getPublisher(String name);
}
