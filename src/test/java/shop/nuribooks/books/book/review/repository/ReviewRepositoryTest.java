package shop.nuribooks.books.book.review.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
@Import({QuerydslConfiguration.class})
public class ReviewRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private PublisherRepository publisherRepository;
	@Autowired
	private GradeRepository gradeRepository;

	private Member member;
	private Book book;
	private List<Review> reviews = new LinkedList<>();

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();
		Grade grade = TestUtils.creategrade();
		gradeRepository.save(grade);
		member = TestUtils.createMember(customer, grade);
		memberRepository.save(member);

		Publisher publisher = TestUtils.createPublisher();
		publisherRepository.save(publisher);

		book = TestUtils.createBook(publisher);
		bookRepository.save(book);
		Book book2 = TestUtils.createBook(publisher);
		bookRepository.save(book2);

		reviews.add(TestUtils.createReview(member, book));
		reviews.add(TestUtils.createReview(member, book2));
		Review updatedReview = TestUtils.createReview(member, book2);
		ReflectionTestUtils.setField(updatedReview, "updateAt", LocalDateTime.now());
		reviews.add(updatedReview);
		for (Review review : reviews) {
			reviewRepository.save(review);
		}
		reviews.removeLast();
	}

	@Test
	void getReviewWithBookIdTest() {
		List<ReviewMemberResponse> response = reviewRepository.findReviewsByBookId(book.getId());
		assertEquals(response.size(), 1);
	}

	@Test
	void getScoreWithBookIdTest() {
		double response = reviewRepository.findScoreByBookId(book.getId());
		assertEquals(response, reviews.get(0).getScore());
	}

	@Test
	void getReviewWithMemberIdTest() {
		List<ReviewBookResponse> response = reviewRepository.findReviewsByMemberId(member.getId());
		assertEquals(response.size(), reviews.size());
	}
}
