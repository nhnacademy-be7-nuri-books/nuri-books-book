package shop.nuribooks.books.book.booklike.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.entity.BookLike;
import shop.nuribooks.books.book.booklike.entity.BookLikeId;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
@Import({QuerydslConfiguration.class})
class BookLikeRepositoryTest {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@Autowired
	private BookLikeRepository bookLikeRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Publisher publisher;
	private Book book1;
	private Book book2;
	private Member member;
	private BookLikeId bookLikeId1;
	private BookLike bookLike1;
	private BookLikeId bookLikeId2;
	private BookLike bookLike2;

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();

		publisher = TestUtils.createPublisher();
		publisherRepository.save(publisher);

		book1 = TestUtils.createBook(publisher);
		bookRepository.save(book1);

		book2 = TestUtils.createBook(publisher);
		bookRepository.save(book2);

		Grade grade = TestUtils.creategrade();
		gradeRepository.save(grade);
		member = TestUtils.createMember(customer, grade);
		memberRepository.save(member);

		bookLikeId1 = new BookLikeId(member.getId(), book1.getId());
		bookLike1 = new BookLike(bookLikeId1, book1);
		bookLikeRepository.save(bookLike1);

		bookLikeId2 = new BookLikeId(member.getId(), book2.getId());
		bookLike2 = new BookLike(bookLikeId2, book2);
		bookLikeRepository.save(bookLike2);
	}

	@Test
	void existsByMemberIdAndBookId() {
		boolean exists = bookLikeRepository.existsByMemberIdAndBookId(member.getId(), book1.getId());
		assertTrue(exists);
	}

	@Test
	void findLikedBooks() {
		Page<BookLikeResponse> likeResponses = bookLikeRepository.findLikedBooks(member.getId(), PageRequest.of(0, 2));
		assertEquals(2, likeResponses.getSize());
	}

}
