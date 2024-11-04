// package shop.nuribooks.books.book.review.repository;
//
// import static java.math.BigDecimal.*;
// import static shop.nuribooks.books.member.member.entity.AuthorityEnum.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
// import shop.nuribooks.books.book.book.entitiy.Book;
// import shop.nuribooks.books.book.book.repository.BookRepository;
// import shop.nuribooks.books.book.review.entity.Review;
// import shop.nuribooks.books.book.review.entity.ReviewImage;
// import shop.nuribooks.books.member.customer.entity.Customer;
// import shop.nuribooks.books.member.customer.repository.CustomerRepository;
// import shop.nuribooks.books.member.grade.entity.Grade;
// import shop.nuribooks.books.member.grade.repository.GradeRepository;
// import shop.nuribooks.books.member.member.entity.Member;
// import shop.nuribooks.books.member.member.entity.StatusEnum;
// import shop.nuribooks.books.member.member.repository.MemberRepository;
//
// @DataJpaTest
// public class ReviewRepositoryTest {
// 	@Autowired
// 	private CustomerRepository customerRepository;
//
// 	@Autowired
// 	private MemberRepository memberRepository;
//
// 	@Autowired
// 	private GradeRepository gradeRepository;
//
// 	@Autowired
// 	private BookRepository bookRepository;
//
// 	@Autowired
// 	private ReviewRepository reviewRepository;
//
// 	@Test
// 	void createReviewTest() {
// 		Customer customer = this.createCustomer();
// 		Grade grade = this.creategrade();
// 		Member member = this.createMember(customer, grade);
// 	}
//
// 	private Review createReview(Member member, Book book) {
// 		return Review.builder()
// 			.title("제목")
// 			.content("내용")
// 			.score(5)
// 			.member(member)
// 			.book(book)
// 			.build();
// 	}
//
// 	private ReviewImage createReviewImage(Review review) {
// 		return ReviewImage.builder()
// 			.imageUrl("hihihi")
// 			.review(review)
// 			.build();
// 	}
//
// 	private Member createMember(Customer customer, Grade grade) {
// 		Member member = Member.builder()
// 			.customer(customer)
// 			.authority(MEMBER)
// 			.grade(grade)
// 			.userId("nuriaaaaaa")
// 			.status(StatusEnum.ACTIVE)
// 			.birthday(LocalDate.of(1988, 8, 12))
// 			.createdAt(LocalDateTime.now())
// 			.point(ZERO)
// 			.totalPaymentAmount(ZERO)
// 			.latestLoginAt(null)
// 			.withdrawnAt(null)
// 			.build();
// 		return memberRepository.save(member);
// 	}
//
// 	private Customer createCustomer() {
// 		Customer customer = Customer.builder()
// 			.name("name")
// 			.password("password")
// 			.phoneNumber("042-8282-8282")
// 			.email("nhnacademy@nuriBooks.com")
// 			.build();
// 		return customerRepository.save(customer);
// 	}
//
// 	private Grade creategrade() {
// 		Grade grade = Grade.builder()
// 			.name("STANDARD")
// 			.pointRate(3)
// 			.requirement(BigDecimal.valueOf(100_000))
// 			.build();
// 		return gradeRepository.save(grade);
// 	}
// }
