package shop.nuribooks.books.member.cart.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.netflix.discovery.converters.Auto;
import com.querydsl.apt.jpa.JPAConfiguration;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.member.cart.entity.Cart;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
@Import(JPAConfiguration.class)
public class CartRepositoryTest {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private BookRepository bookRepository;

	private Customer savedCustomer;
	private Grade savedGrade;
	private Member savedMember;
	private Publisher savedPublisher;
	private Book savedBook;
	private Cart savedCart;

	@BeforeEach
	void setUp() {
		Customer customer = Customer.builder()
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();

		savedCustomer = customerRepository.save(customer);

		Grade grade = Grade.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();

		savedGrade = gradeRepository.save(grade);

		Member member = Member.builder()
			.customer(savedCustomer)
			.authority(AuthorityType.MEMBER)
			.grade(savedGrade)
			.status(StatusType.ACTIVE)
			.gender(GenderType.MALE)
			.userId("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.latestLoginAt(LocalDateTime.now())
			.build();

		savedMember = memberRepository.save(member);

		Publisher publisher = Publisher.builder()
			.name("nuri")
			.build();

		savedPublisher = publisherRepository.save(publisher);

		Book book = Book.builder()
			.publisherId(savedPublisher)
			.state(BookStateEnum.NORMAL)
			.title("Original Book Title")
			.thumbnailImageUrl("original_thumbnail.jpg")
			.detailImageUrl("original_detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.description("Original Description")
			.contents("Original Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();

		savedBook = bookRepository.save(book);

		Cart cart = Cart.builder()
			.member(savedMember)
			.book(savedBook)
			.quantity(3)
			.build();

		savedCart = cartRepository.save(cart);
	}

	@DisplayName("회원 아이디로 장바구니 목록 조회")
	@Test
	void findAllByMemberId() {
	    //given

	    //when
		List<Cart> result = cartRepository.findAllByMemberId(savedMember.getId());

		//then
		assertThat(result.size()).isEqualTo(1);
	}
}
