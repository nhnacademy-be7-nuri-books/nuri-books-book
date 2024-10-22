package shop.nuribooks.books.service.contributor;

import shop.nuribooks.books.dto.contributor.ContributorReq;
import shop.nuribooks.books.entity.book.Contributor;

public interface ContributorService {
	Contributor registerContributor(ContributorReq req);

	Contributor updateContributor(Long contributorId, ContributorReq req);

	void deleteContributor(Long contributorId);

}
