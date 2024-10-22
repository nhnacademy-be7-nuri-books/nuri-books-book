package shop.nuribooks.books.service.contributor;

import shop.nuribooks.books.dto.contributor.ContributorReq;
import shop.nuribooks.books.entity.Contributors;

public interface ContributorService {
	Contributors registerContributor(ContributorReq req);

}
