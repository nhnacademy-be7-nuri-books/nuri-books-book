package shop.nuribooks.books.book.bookcontributor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.nuribooks.books.book.TestUtils;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorRegisterRequest;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.contributor.entitiy.Contributor;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRole;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookContributorServiceImplTest {

    @InjectMocks
    private BookContributorServiceImpl bookContributorService;

    @Mock
    private BookContributorRepository bookContributorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ContributorRepository contributorRepository;

    @Mock
    private ContributorRoleRepository contributorRoleRepository;

    private Book book;
    private Contributor contributor;
    private ContributorRole contributorRole;
    private BookContributorRegisterRequest registerRequest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = Book.builder()
                .publisherId(new Publisher(1L, "Sample Publisher"))
                .state(BookStateEnum.NEW)
                .title("Sample Book")
                .thumbnailImageUrl("https://example.com/thumbnail.jpg")
                .detailImageUrl("https://example.com/detail.jpg")
                .publicationDate(LocalDate.now())
                .price(BigDecimal.valueOf(29.99))
                .discountRate(10)
                .description("Sample description.")
                .contents("Sample contents.")
                .isbn("978-3-16-148410-0")
                .isPackageable(true)
                .stock(100)
                .likeCount(0)
                .viewCount(0L)
                .build();

        TestUtils.setIdForEntity(book, 1L);

        contributor = Contributor.builder().id(1L).name("contributor").build();
        contributorRole = new ContributorRole(1L, ContributorRoleEnum.AUTHOR);

        registerRequest = new BookContributorRegisterRequest(1L, 1L, List.of(1L));

    }

    @DisplayName("도서 기여자 등록 성공")
    @Test
    void testRegisterContributorToBook_Success() {
        when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.of(book));
        when(contributorRepository.findById(registerRequest.contributorId())).thenReturn(Optional.of(contributor));
        when(contributorRoleRepository.findById(1L)).thenReturn(Optional.of(contributorRole));

        bookContributorService.registerContributorToBook(registerRequest);

        verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
    }

    @DisplayName("도서 기여자 등록 실패 - 책 not found")
    @Test
    void testRegisterContributorToBook_BookNotFound() {
        when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookContributorService.registerContributorToBook(registerRequest));
        verify(bookContributorRepository, never()).save(any(BookContributor.class));
    }

    @DisplayName("도서 기여자 등록 실패 - 기여자 not found")
    @Test
    void testRegisterContributorToBook_ContributorNotFound() {
        when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.of(book));
        when(contributorRepository.findById(registerRequest.contributorId())).thenReturn(Optional.empty());

        assertThrows(ContributorNotFoundException.class, () -> bookContributorService.registerContributorToBook(registerRequest));
        verify(bookContributorRepository, never()).save(any(BookContributor.class));
    }


    @DisplayName("도서 기여자 등록 실패 - 기여자 역할 not found")
    @Test
    void testRegisterContributorToBook_ContributorRoleNotFound() {
        when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.of(book));
        when(contributorRepository.findById(registerRequest.contributorId())).thenReturn(Optional.of(contributor));
        when(contributorRoleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ContributorRoleNotFoundException.class, () -> bookContributorService.registerContributorToBook(registerRequest));
        verify(bookContributorRepository, never()).save(any(BookContributor.class));
    }

    }