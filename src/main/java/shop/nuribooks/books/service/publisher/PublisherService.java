package shop.nuribooks.books.service.publisher;

import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;

public interface PublisherService {
	//TODO: 등록, 조회, 수정, 삭제 구현
	PublisherResponse registerPublisher(PublisherRequest request);
}
