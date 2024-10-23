package shop.nuribooks.books.service.contributor;

import shop.nuribooks.books.dto.contributor.ContributorRequest;
import shop.nuribooks.books.dto.contributor.ContributorResponse;

public interface ContributorService {
	ContributorResponse registerContributor(ContributorRequest req);

	ContributorResponse updateContributor(Long contributorId, ContributorRequest req);

	ContributorResponse getContributor(Long contributorId);

	void deleteContributor(Long contributorId);

}
