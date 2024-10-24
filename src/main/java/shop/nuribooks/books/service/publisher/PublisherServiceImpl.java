package shop.nuribooks.books.service.publisher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;
import shop.nuribooks.books.entity.book.Publisher;
import shop.nuribooks.books.exception.publisher.PublisherAlreadyExistsException;
import shop.nuribooks.books.repository.publisher.PublisherRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
	private final PublisherRepository publisherRepository;

	/**
	 * registerPublisher : 출판사 이름 등록
	 * @author kyongmin
	 *
	 * @param request 출판사 이름 등록
	 *                이미 존재하는 출판사일 경우 PublisherAlreadyExistsException 발생
	 * @return 저장된 출판사 이름을 포함한 PublisherResponse
	 */
	@Override
	public PublisherResponse registerPublisher(PublisherRequest request) {
		Publisher publisher = new Publisher();
		if (publisherRepository.existsByName(request.name())) {
			throw new PublisherAlreadyExistsException("출판사가 이미 등록되어 있습니다.");
		}
		publisher.setName(request.name());
		Publisher saved = publisherRepository.save(publisher);
		return new PublisherResponse(saved.getName());
	}
}
