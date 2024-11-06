package shop.nuribooks.books.book.bookcontributor.dto;

import lombok.Builder;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRoleEnum;

@Builder
public record BookContributorInfoResponse(Long contributorId,
                                          String contributorName,
                                          Long contributorRoleId,
                                          ContributorRoleEnum contributorRoleName) {

    public static BookContributorInfoResponse of(Long contributorId,
                                                 String contributorName,
                                                 Long contributorRoleId,
                                                 ContributorRoleEnum contributorRoleName) {
        return new BookContributorInfoResponse(contributorId, contributorName, contributorRoleId, contributorRoleName);
    }
}