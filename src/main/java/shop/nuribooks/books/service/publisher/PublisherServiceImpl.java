package shop.nuribooks.books.service.publisher;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;
import shop.nuribooks.books.entity.book.Publisher;
import shop.nuribooks.books.exception.publisher.PublisherAlreadyExistsException;
import shop.nuribooks.books.exception.publisher.PublisherNotFoundException;
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
		if (publisherRepository.existsByName(request.name())) {
			throw new PublisherAlreadyExistsException("출판사가 이미 등록되어 있습니다.");
		}
		Publisher publisher = request.toEntity();
		Publisher saved = publisherRepository.save(publisher);
		return PublisherResponse.of(saved);
	}

	/**
	 * getAllPublisher : 등록 되어있는 모든 출판사 정보 조회
	 * @author kyongmin
	 *
	 * @return 등록된 출판사 정보를 포함한 PublisherResponse List
	 */
	@Override
	public List<PublisherResponse> getAllPublisher() {
		List<Publisher> publishers = publisherRepository.findAll().stream().toList();
		return publishers.stream()
			.map(PublisherResponse::of)
			.toList();
	}

	/**
	 * getPublisher : 출판사 정보 조회
	 * @author kyongmin
	 *
	 * @param name 이름으로 출판사 정보 조회
	 *             존재하지 않는 출판사일 경우 PublisherNotFoundException 발생
	 * @return 해당하는 출판사 정보를 포함한 PublisherResponse
	 */
	@Override
	public PublisherResponse getPublisher(String name) {
		Publisher publisher = publisherRepository.findByName(name)
			.orElseThrow(() -> new PublisherNotFoundException("출판사가 존재하지 않습니다."));
		return PublisherResponse.of(publisher);
	}

	/**
	 * deletePublisher : 출판사 정보 삭제
	 * @author kyongmin
	 *
	 * @param name 이름으로 출판사 정보 삭제
	 *             존재하지 않는 출판사일 경우 PublisherNotFoundException 발생
	 */
	@Override
	public void deletePublisher(String name) {
		Publisher publisher = publisherRepository.findByName(name)
			.orElseThrow(() -> new PublisherNotFoundException("출판사가 존재하지 않습니다."));

		publisherRepository.delete(publisher);
	}
}
