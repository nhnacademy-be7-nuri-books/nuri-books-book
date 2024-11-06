package shop.nuribooks.books.member.member.repository;

import static org.springframework.data.support.PageableExecutionUtils.*;
import static org.springframework.util.StringUtils.*;
import static shop.nuribooks.books.member.customer.entity.QCustomer.*;
import static shop.nuribooks.books.member.grade.entity.QGrade.*;
import static shop.nuribooks.books.member.member.entity.QMember.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.StatusType;

/**
 * @author Jprotection
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	// 터무니없이 큰 페이지 사이즈 요청에 대해 서버를 보호하기 위한 maxPageSize 설정
	private static final int MAX_PAGE_SIZE = 100;

	public Page<MemberSearchResponse> searchMembersWithPaging(MemberSearchRequest request, Pageable pageable) {

		int validPageSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
		Pageable validPageable = PageRequest.of(pageable.getPageNumber(), validPageSize);

		List<MemberSearchResponse> content = queryFactory
			.select(Projections.constructor(MemberSearchResponse.class,
				customer.id,
				customer.name,
				member.gender,
				customer.phoneNumber,
				customer.email,
				member.birthday,
				member.userId,
				member.point,
				member.totalPaymentAmount,
				member.authority,
				grade.name,
				member.status,
				member.createdAt,
				member.latestLoginAt))
			.from(member)
			.leftJoin(member.customer, customer)
			.leftJoin(member.grade, grade)
			.where(
				nameContains(request.name()),
				genderEquals(request.gender()),
				phoneNumberContains(request.phoneNumber()),
				emailContains(request.email()),
				birthdayGoe(request.birthdayGoe()),
				birthdayLoe(request.birthdayLoe()),
				userIdContains(request.userId()),
				pointGoe(request.pointGoe()),
				pointLoe(request.pointLoe()),
				totalPaymentAmountGoe(request.totalPaymentAmountGoe()),
				totalPaymentAmountLoe(request.totalPaymentAmountLoe()),
				authorityEquals(request.authority()),
				gradeNameContains(request.gradeName().toUpperCase()),
				statusEquals(request.status()),
				createdAtGoe(request.createdAtGoe()),
				createdAtLoe(request.createdAtLoe()),
				latestLoginAtGoe(request.latestLoginAtGoe()),
				latestLoginAtLoe(request.latestLoginAtLoe()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(Wildcard.count)
			.from(member)
			.leftJoin(member.customer, customer)
			.leftJoin(member.grade, grade)
			.where(
				nameContains(request.name()),
				genderEquals(request.gender()),
				phoneNumberContains(request.phoneNumber()),
				emailContains(request.email()),
				birthdayGoe(request.birthdayGoe()),
				birthdayLoe(request.birthdayLoe()),
				userIdContains(request.userId()),
				pointGoe(request.pointGoe()),
				pointLoe(request.pointLoe()),
				totalPaymentAmountGoe(request.totalPaymentAmountGoe()),
				totalPaymentAmountLoe(request.totalPaymentAmountLoe()),
				authorityEquals(request.authority()),
				gradeNameContains(request.gradeName()),
				statusEquals(request.status()),
				createdAtGoe(request.createdAtGoe()),
				createdAtLoe(request.createdAtLoe()),
				latestLoginAtGoe(request.latestLoginAtGoe()),
				latestLoginAtLoe(request.latestLoginAtLoe())
			);

		return getPage(content, validPageable, countQuery::fetchOne);
	}


	// 동적 쿼리 메서드 목록
	private BooleanExpression nameContains(String name) {
		return hasText(name) ? customer.name.contains(name) : null;
	}

	private BooleanExpression genderEquals(GenderType gender) {
		return gender != null ? member.gender.eq(gender) : null;
	}

	private BooleanExpression phoneNumberContains(String phoneNumber) {
		return hasText(phoneNumber) ? customer.phoneNumber.contains(phoneNumber) : null;
	}

	private BooleanExpression emailContains(String email) {
		return hasText(email) ? customer.email.contains(email) : null;
	}

	private BooleanExpression birthdayGoe(LocalDate birthdayGoe) {
		return birthdayGoe != null	? member.birthday.goe(birthdayGoe) : null;
	}

	private BooleanExpression birthdayLoe(LocalDate birthdayLoe) {
		return birthdayLoe != null	? member.birthday.loe(birthdayLoe) : null;
	}

	private BooleanExpression userIdContains(String userId) {
		return hasText(userId) ? member.userId.contains(userId) : null;
	}

	private BooleanExpression pointGoe(BigDecimal pointGoe) {
		return pointGoe != null ? member.point.goe(pointGoe) : null;
	}

	private BooleanExpression pointLoe(BigDecimal pointLoe) {
		return pointLoe != null ? member.point.loe(pointLoe) : null;
	}

	private BooleanExpression totalPaymentAmountGoe(BigDecimal totalPaymentAmountGoe) {
		return totalPaymentAmountGoe != null ? member.totalPaymentAmount.goe(totalPaymentAmountGoe) : null;
	}

	private BooleanExpression totalPaymentAmountLoe(BigDecimal totalPaymentAmountLoe) {
		return totalPaymentAmountLoe != null ? member.totalPaymentAmount.loe(totalPaymentAmountLoe) : null;
	}

	private BooleanExpression authorityEquals(AuthorityType authority) {
		return authority != null ? member.authority.eq(authority) : null;
	}

	private BooleanExpression gradeNameContains(String gradeName) {
		return hasText(gradeName) ? grade.name.contains(gradeName) : null;
	}

	private BooleanExpression statusEquals(StatusType status) {
		return status != null ? member.status.eq(status) : null;
	}

	private BooleanExpression createdAtGoe(LocalDateTime createdAtGoe) {
		return createdAtGoe != null ? member.createdAt.goe(createdAtGoe) : null;
	}

	private BooleanExpression createdAtLoe(LocalDateTime createdAtLoe) {
		return createdAtLoe != null ? member.createdAt.loe(createdAtLoe) : null;
	}

	private BooleanExpression latestLoginAtGoe(LocalDateTime latestLoginAtGoe) {
		return latestLoginAtGoe != null ? member.latestLoginAt.goe(latestLoginAtGoe) : null;
	}

	private BooleanExpression latestLoginAtLoe(LocalDateTime latestLoginAtLoe) {
		return latestLoginAtLoe != null ? member.latestLoginAt.loe(latestLoginAtLoe) : null;
	}
}
