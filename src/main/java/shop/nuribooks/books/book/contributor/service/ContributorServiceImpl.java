package shop.nuribooks.books.book.contributor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.entity.ContributorEditor;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;

import java.util.List;

/**
 * @author kyongmin
 */
@Transactional
@RequiredArgsConstructor
@Service
public class ContributorServiceImpl implements ContributorService {

	private final ContributorRepository contributorRepository;

	/**
	 * registerContributor : 기여자 등록
	 *
	 * @param request 기여자 이름 등록
	 * @return 등록된 기여자 이름을 포함한 ContributorResponse
	 */
	@Override
	public ContributorResponse registerContributor(ContributorRequest request) {

		Contributor contributor = request.toEntity();
		Contributor savedContributor = contributorRepository.save(contributor);
		return ContributorResponse.of(savedContributor);

	}

	/**
	 * updateContributor : 기여자 수정
	 * @param contributorId contributorId로 기여자 정보 조회
	 *                      존재하지 않는 기여자일 경우 ContributorNotFoundException 발생
	 * @param request 수정할 기여자 정보를 담은 객체
	 * @return contributorId에 해당하는 수정된 기여자 정보를 포함한 ContributorResponse
	 */
	@Override
	public ContributorResponse updateContributor(Long contributorId, ContributorRequest request) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(
				() -> new ContributorNotFoundException("해당 기여자가 존재하지 않습니다."));

		ContributorEditor contributorEditor = getContributorEditor(request, contributor);
		contributor.edit(contributorEditor);
		return ContributorResponse.of(contributor);

	}

	/**
	 * getContributor : 기여자 조회
	 *
	 * @param contributorId contributorId로 기여자 정보 조회
	 *                      존재하지 않는 기여자일 경우 ContributorNotFoundException 발생
	 * @return contributorId에 해당하는 기여자 정보를 포함한 ContributorResponse
	 */
	@Override
	public ContributorResponse getContributor(Long contributorId) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException("해당 기여자가 존재하지 않습니다."));
		return ContributorResponse.of(contributor);
	}

	/**
	 * getAllContributors : 등록 되어있는 모든 기여자 정보 조회
	 *
	 * @return 등록된 기여자 정보를 포함한 ContributorResponse List
	 */
	@Override
	public Page<ContributorResponse> getAllContributors(Pageable pageable) {
		Page<Contributor> contributors = contributorRepository.findAll(pageable);
		return contributors.map(ContributorResponse::of);
	}

	/**
	 * deleteContributor : 기여자 삭제
	 *
	 * @param contributorId contributorId로 기여자 정보 조회
	 *                      존재하지 않는 기여자일 경우 ContributorNotFoundException 발생
	 */
	@Override
	public void deleteContributor(Long contributorId) {
		Contributor contributor = contributorRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException("해당 기여자가 존재하지 않습니다."));
		contributorRepository.delete(contributor);
	}

	/**
	 * getContributorEditor : 기여자 편집(수정) 빌더
	 * @param request 요청된 기여자 정보 담긴 객체
	 * @param contributor 기존 기여자 정보를 담은 객체
	 * @return 수정된 정보를 담은 객체
	 */
	private ContributorEditor getContributorEditor(ContributorRequest request, Contributor contributor) {
		ContributorEditor.ContributorEditorBuilder builder = contributor.toEditor();
		return builder
			.name(request.name())
			.build();
	}
}
