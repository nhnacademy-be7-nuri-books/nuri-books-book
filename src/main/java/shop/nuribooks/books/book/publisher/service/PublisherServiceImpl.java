package shop.nuribooks.books.book.publisher.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.publisher.dto.PublisherRequest;
import shop.nuribooks.books.book.publisher.dto.PublisherResponse;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.publisher.entitiy.PublisherEditor;
import shop.nuribooks.books.exception.publisher.PublisherAlreadyExistsException;
import shop.nuribooks.books.exception.publisher.PublisherNotFoundException;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;

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

	/**
	 * updatePublisher : 출판사 정보 수정
	 * @author kyongmin
	 *
	 * @param name 수정할 출판사 기존 이름
	 * 존재하지 않는 출판사일 경우 PublisherNotFoundException 발생
	 * @param request 수정할 출판사 이름이 포함된 요청 객체
	 * @return 수정된 출판사 이름을 포함한 PublisherResponse
	 */
	@Override
	public PublisherResponse updatePublisher(String name, PublisherRequest request) {
		Publisher publisher = publisherRepository.findByName(name)
			.orElseThrow(() -> new PublisherNotFoundException("출판사가 존재하지 않습니다."));

		PublisherEditor publisherEditor = getPublisherEditor(request, publisher);
		publisher.edit(publisherEditor);
		return PublisherResponse.of(publisher);
	}

	/**
	 * getPublisherEditor : 수정사항을 반영한 빌더 생성
	 *
	 * @param request 수정된 정보가 담긴 요청 객체
	 * @param publisher 기존 출판사 엔티티
	 * @return 수정된 출판사 정보를 포함한 PublisherEditor
	 */
	private static PublisherEditor getPublisherEditor(PublisherRequest request, Publisher publisher) {
		PublisherEditor.PublisherEditorBuilder builder = publisher.toEditor();
		return builder
			.name(request.name())
			.build();
	}
}
