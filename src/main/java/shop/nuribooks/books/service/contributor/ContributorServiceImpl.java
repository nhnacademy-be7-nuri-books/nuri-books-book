package shop.nuribooks.books.service.contributor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributor.ContributorReq;
import shop.nuribooks.books.entity.book.Contributor;
import shop.nuribooks.books.repository.contributor.ContributorRepository;

@Service
@RequiredArgsConstructor
public class ContributorServiceImpl implements ContributorService {

	private final ContributorRepository contributorRepository;

	@Override
	public Contributor registerContributor(ContributorReq req) {

		Contributor savedContributor = new Contributor();
		savedContributor.setName(req.getName());
		return contributorRepository.save(savedContributor);

	}
}