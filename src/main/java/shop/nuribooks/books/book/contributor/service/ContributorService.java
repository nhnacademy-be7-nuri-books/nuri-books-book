package shop.nuribooks.books.book.contributor.service;

import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContributorService {
	ContributorResponse registerContributor(ContributorRequest req);

	ContributorResponse updateContributor(Long contributorId, ContributorRequest req);

	ContributorResponse getContributor(Long contributorId);

	Page<ContributorResponse> getAllContributors(Pageable pageable);

	void deleteContributor(Long contributorId);

}
