package shop.nuribooks.books.service.contributor;

import shop.nuribooks.books.dto.contributor.ContributorReqDto;
import shop.nuribooks.books.dto.contributor.ContributorResDto;

public interface ContributorService {
	ContributorResDto registerContributor(ContributorReqDto req);

	ContributorResDto updateContributor(Long contributorId, ContributorReqDto req);

	ContributorResDto getContributor(Long contributorId);

	void deleteContributor(Long contributorId);

}
