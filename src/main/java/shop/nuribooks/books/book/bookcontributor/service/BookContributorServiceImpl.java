package shop.nuribooks.books.book.bookcontributor.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorRegisterRequest;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.contributor.entitiy.Contributor;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRole;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kyongmin
 */
@RequiredArgsConstructor
@Service
public class BookContributorServiceImpl implements BookContributorService {

    private final BookContributorRepository bookContributorRepository;
    private final BookRepository bookRepository;
    private final ContributorRepository contributorRepository;
    private final ContributorRoleRepository contributorRoleRepository;

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
                .orElseThrow(() -> new ContributorNotFoundException("기여자가 존재하지 않습니다."));

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

}



