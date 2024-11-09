package shop.nuribooks.books.book.contributor.service;

import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;

import java.util.List;
public interface ContributorService {
	ContributorResponse registerContributor(ContributorRequest req);

	ContributorResponse updateContributor(Long contributorId, ContributorRequest req);

	ContributorResponse getContributor(Long contributorId);

	List<ContributorResponse> getAllContributors();

	void deleteContributor(Long contributorId);

}
