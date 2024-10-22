package shop.nuribooks.books.service.contributor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributor.ContributorReq;
import shop.nuribooks.books.entity.Contributors;
import shop.nuribooks.books.repository.contributor.ContributorRepository;

@Service
@RequiredArgsConstructor
public class ContributorServiceImpl implements ContributorService {

	private final ContributorRepository contributorRepository;

	@Override
	public void registerContributor(ContributorReq req) {

		Contributors savedContributor = new Contributors();
		savedContributor.setName(req.getName());
		contributorRepository.save(savedContributor);

	}
}