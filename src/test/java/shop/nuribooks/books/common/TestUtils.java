package shop.nuribooks.books.common;

import static shop.nuribooks.books.member.member.entity.AuthorityType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.review.entity.Review;
import shop.nuribooks.books.book.review.entity.ReviewImage;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;

public class TestUtils {

	public static Review createReview(Member member, Book book) {
		Review review = Review.builder()
			.title("제목")
			.content("내용")
			.score(5)
			.member(member)
			.book(book)
			.build();
		review.getReviewImages().add(createReviewImage("image1", review));
		review.getReviewImages().add(createReviewImage("image2", review));
		return review;
	}

	private static ReviewImage createReviewImage(String imageUrl, Review review) {
		return ReviewImage.builder()
			.imageUrl(imageUrl)
			.review(review)
			.build();
	}

	public static Member createMember(Customer customer, Grade grade) {
		return Member.builder()
			.gender(GenderType.MALE)
			.customer(customer)
			.authority(MEMBER)
			.grade(grade)
			.userId("nuriaaaaaa")
			.status(StatusType.ACTIVE)
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.latestLoginAt(null)
			.withdrawnAt(null)
			.build();
	}

	public static Customer createCustomer() {
		return Customer.builder()
			.name("name")
			.password("password")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}

	public static Grade creategrade() {
		return Grade.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	public static Publisher createPublisher() {
		return new Publisher("Publisher Name");
	}

	public static Book createBook(Publisher publisher) {
		return Book.builder()
			.state(BookStateEnum.OUT_OF_PRINT)
			.publisherId(publisher)
			.title("Test Book Title")
			.thumbnailImageUrl("thumbnail.jpg")
			.detailImageUrl("detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(10000))
			.discountRate(10)
			.description("Test Book Description")
			.contents("Test Book Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.likeCount(0)
			.stock(100)
			.viewCount(0L)
			.build();
	}
}
