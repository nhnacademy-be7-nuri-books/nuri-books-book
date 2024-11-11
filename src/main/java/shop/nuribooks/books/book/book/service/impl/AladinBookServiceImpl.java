package shop.nuribooks.books.book.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.dto.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.AladinBookSaveRequest;
import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.AladinBookService;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.client.AladinFeignClient;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AladinBookServiceImpl implements AladinBookService {
	private final AladinFeignClient aladinFeignClient;

	@Value("${aladin.api.key}")
	private String ttbKey;

	//도서 리스트 조회 메서드
	//TODO: 추후 파라미터 추가 예정
	@Override
	public List<AladinBookListItemResponse> getNewBooks(String queryType, String searchTarget, int maxResults) {
		try {
			AladinBookListResponse response = aladinFeignClient.getNewBooks(ttbKey, queryType, maxResults, 1, "Book", "JS", "20131101");
			log.info("Received BookList response: {}", response);
			return response.item();
		} catch (Exception ex) {
			log.error("Error: ", ex);
			throw new RuntimeException("Failed to retrieve new books from Aladin");
		}
	}

	//TODO: 도서목록 조회
	@Override
	public AladinBookListItemResponse getBookByIsbnWithAladin(String isbn) {
		try {
			AladinBookListResponse response = aladinFeignClient.getBookDetails(ttbKey, "ISBN", isbn, "JS", "20131101");
			log.info("Received Book response: {}", response);
			return response.item().getFirst();
		} catch (Exception ex) {
			log.error("Error: ", ex);
			throw new ResourceNotFoundException("도서를 찾을 수 없습니다.");
		}
	}
}
