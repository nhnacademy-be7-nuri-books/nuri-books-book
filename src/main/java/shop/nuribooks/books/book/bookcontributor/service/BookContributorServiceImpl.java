package shop.nuribooks.books.book.bookcontributor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorRegisterRequest;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.contributor.BookContributorNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;

/**
 * @author kyongmin
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BookContributorServiceImpl implements BookContributorService {

	private final BookContributorRepository bookContributorRepository;
	private final BookRepository bookRepository;
	private final ContributorRepository contributorRepository;
	private final ContributorRoleRepository contributorRoleRepository;
	private final BookMapper bookMapper;

	/**
	 * registerContributorToBook : 도서에 기여자와 기여자 역할 등록
	 *
	 * @param registerRequest 등록할 기여자 정보와 도서 정보를 담은 객체
	 */
	@Override
	@Transactional
	public void registerContributorToBook(BookContributorRegisterRequest registerRequest) {

		Book book = bookRepository.findById(registerRequest.bookId())
			.orElseThrow(() -> new BookNotFoundException(registerRequest.bookId()));

		Contributor contributor = contributorRepository.findById(registerRequest.contributorId())
			.orElseThrow(() -> new ContributorNotFoundException());

		List<ContributorRole> roles = new ArrayList<>();
		for (Long roleId : registerRequest.contributorRoleId()) {
			ContributorRole role = contributorRoleRepository.findById(roleId)
				.orElseThrow(() -> new ContributorRoleNotFoundException("역할이 존재하지 않습니다."));
			roles.add(role);
		}

		for (ContributorRole role : roles) {
			BookContributor bookContributor = BookContributor.builder()
				.book(book)
				.contributor(contributor)
				.contributorRole(role)
				.build();
			bookContributorRepository.save(bookContributor);
		}
	}

	//TODO: QUERYDSL로 변경해야됨

	/**
	 * getAllBooksByContributorId : 기여자 id 로 모든 도서 조회
	 *
	 * @param contributorId 도서 목록 조회할 기여자 id
	 * @return 도서 정보가 포함된 리스트 형식의 BookResponse
	 */
	@Override
	public List<BookResponse> getAllBooksByContributorId(Long contributorId) {
		contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException());

		List<Long> bookIds = bookContributorRepository.findBookIdsByContributorId(contributorId);
		List<Book> books = bookRepository.findAllById(bookIds);

		return books.stream()
			.map(bookMapper::toBookResponse)
			.collect(Collectors.toList());
	}

	/**
	 * getContributorsAndRolesByBookId : 도서 id로 기여자와 기여자 역할 조회
	 *
	 * @param bookId 기여자와 기여자 역할 조회할 도서 id
	 * @return 도서 기여자 정보가 포함된 리스트 형식의 BookContributorInfoResponse
	 */
	@Override
	public List<BookContributorInfoResponse> getContributorsAndRolesByBookId(Long bookId) {
		try {
			log.info("Fetching contributors for bookId: {}", bookId);
			List<BookContributorInfoResponse> contributors = bookContributorRepository.findContributorsAndRolesByBookId(
				bookId);
			log.info("Contributors fetched: {}", contributors);
			return contributors;
		} catch (Exception e) {
			log.error("Error fetching contributors for bookId: {}", bookId, e);
			throw e;
		}
	}

	/**
	 * deleteBookContributor : 도서 기여자 id 삭제
	 *
	 * @param bookContributorId 삭제할 도서 기여자 id
	 */
	@Override
	public void deleteBookContributor(Long bookContributorId) {
		BookContributor bookContributor = bookContributorRepository.findById(bookContributorId)
			.orElseThrow(() -> new BookContributorNotFoundException("도서 기여자가 존재하지 않습니다."));

		bookContributorRepository.delete(bookContributor);
	}

}



