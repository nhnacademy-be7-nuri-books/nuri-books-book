package shop.nuribooks.books.common;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shop.nuribooks.books.book.book.controller.BookController;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.book.booktag.controller.BookTagController;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.category.controller.BookCategoryController;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.book.point.controller.PointPolicyController;
import shop.nuribooks.books.book.point.service.PointPolicyService;
import shop.nuribooks.books.book.review.controller.ReviewController;
import shop.nuribooks.books.book.review.service.ReviewService;
import shop.nuribooks.books.book.tag.controller.TagController;
import shop.nuribooks.books.book.tag.service.TagServiceImpl;
import shop.nuribooks.books.cart.controller.CartController;
import shop.nuribooks.books.member.address.controller.AddressController;
import shop.nuribooks.books.member.address.service.AddressServiceImpl;
import shop.nuribooks.books.member.customer.controller.CustomerController;
import shop.nuribooks.books.member.customer.service.CustomerService;
import shop.nuribooks.books.member.grade.controller.GradeController;
import shop.nuribooks.books.member.grade.service.GradeService;
import shop.nuribooks.books.member.member.controller.MemberController;
import shop.nuribooks.books.member.member.service.MemberService;

@WebMvcTest(controllers = {
        BookController.class,
        BookTagController.class,
        BookCategoryController.class,
        PointPolicyController.class,
        ReviewController.class,
        TagController.class,
        AddressController.class,
        CustomerController.class,
        GradeController.class,
        MemberController.class,
})
public abstract class ControllerTestSupport {

    @MockBean
    protected BookService bookService;

    @MockBean
    protected BookTagService bookTagService;

    @MockBean
    protected BookCategoryService bookCategoryService;

    @MockBean
    protected PointPolicyService pointPolicyService;

    @MockBean
    protected ReviewService reviewService;

    @MockBean
    protected TagServiceImpl tagService;

    @MockBean
    protected AddressServiceImpl addressService;

    @MockBean
    protected CustomerService customerService;

    @MockBean
    protected GradeService gradeService;

    @MockBean
    protected MemberService memberService;

}
