package shop.nuribooks.books.service.member;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.repository.member.GradeRepository;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

	private final GradeRepository gradeRepository;
}
