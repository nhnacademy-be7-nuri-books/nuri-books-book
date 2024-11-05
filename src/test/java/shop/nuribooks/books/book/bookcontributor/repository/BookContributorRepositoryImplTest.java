package shop.nuribooks.books.book.bookcontributor.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shop.nuribooks.books.book.TestUtils;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.contributor.entitiy.Contributor;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRole;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.dto.PublisherResponse;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.common.config.QuerydslConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class BookContributorRepositoryImplTest {

    @Autowired
    private BookContributorRepository bookContributorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ContributorRepository contributorRepository;

    @Autowired
    private ContributorRoleRepository contributorRoleRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    private Book testBook;
    private Contributor testContributor;
    private ContributorRole testContributorRole;

    @BeforeEach
    void setUp() {
        // Publisher 저장
        Publisher publisher = new Publisher(1L, "Sample Publisher");

        publisher = publisherRepository.save(publisher);
        // Book 엔터티를 먼저 데이터베이스에 저장
        testBook = Book.builder()
                .publisherId(publisher)  // Publisher 설정
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
        bookRepository.save(testBook);  // 데이터베이스에 저장

        // Contributor 저장
        testContributor = Contributor.builder().name("contributor").build();
        contributorRepository.save(testContributor);  // 데이터베이스에 저장

        // ContributorRole 저장
        testContributorRole = new ContributorRole(1L, ContributorRoleEnum.AUTHOR);
        contributorRoleRepository.save(testContributorRole);  // 데이터베이스에 저장

        // BookContributor 저장
        BookContributor bookContributor = BookContributor.builder()
                .book(testBook)
                .contributor(testContributor)
                .contributorRole(testContributorRole)
                .build();
        bookContributorRepository.save(bookContributor);  // 데이터베이스에 저장
    }

    @Test
    @DisplayName("도서 ID로 기여자와 역할 정보를 조회한다")
    void findContributorsAndRolesByBookId() {
        // given
        Long bookId = testBook.getId();

        // when
        List<BookContributorInfoResponse> result = bookContributorRepository.findContributorsAndRolesByBookId(bookId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).contributorId()).isEqualTo(testContributor.getId());
        assertThat(result.get(0).contributorName()).isEqualTo(testContributor.getName());
        assertThat(result.get(0).contributorRoleId()).isEqualTo(testContributorRole.getId());
        assertThat(result.get(0).contributorRoleName()).isEqualTo(testContributorRole.getName());
    }
}
