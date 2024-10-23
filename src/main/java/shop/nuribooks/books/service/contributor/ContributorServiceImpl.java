package shop.nuribooks.books.service.contributor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.contributor.ContributorRequest;
import shop.nuribooks.books.dto.contributor.ContributorResponse;
import shop.nuribooks.books.entity.book.Contributor;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;
import shop.nuribooks.books.repository.contributor.ContributorRepository;

@Service
@RequiredArgsConstructor
public class ContributorServiceImpl implements ContributorService {

	private final ContributorRepository contributorRepository;

	@Override
	public ContributorResponse registerContributor(ContributorRequest req) {

		Contributor contributor = new Contributor();
		contributor.setName(req.getName());
		Contributor savedContributor = contributorRepository.save(contributor);

		return new ContributorResponse(savedContributor.getId(), savedContributor.getName());

	}

	@Override
	public ContributorResponse updateContributor(Long contributorId, ContributorRequest req) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(
				() -> new ContributorNotFoundException("해당 기여자가 존재하지 않습니다."));

		contributor.setName(req.getName());
		Contributor updatedContributor = contributorRepository.save(contributor);
		return new ContributorResponse(updatedContributor.getId(), updatedContributor.getName());

	}

	@Override
	public ContributorResponse getContributor(Long contributorId) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException("해당 기여자가 존재하지 않습니다."));
		return new ContributorResponse(contributor.getId(), contributor.getName());
	}

	@Override
	public void deleteContributor(Long contributorId) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException("해당 기여자가 존재하지 않습니다."));
		contributorRepository.delete(contributor);
	}

}
