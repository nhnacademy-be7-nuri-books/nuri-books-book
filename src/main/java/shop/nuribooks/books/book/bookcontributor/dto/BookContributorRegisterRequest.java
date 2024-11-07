package shop.nuribooks.books.book.bookcontributor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record BookContributorRegisterRequest(@NotNull(message = "bookId는 필수 입력 항목입니다")
                                             @Positive(message = "bookId는 양수여야 합니다")
                                             Long bookId,
                                             @NotNull(message = "contributorId는 필수 입력 항목입니다")
                                             @Positive(message = "contributorId는 양수여야 합니다")
                                             Long contributorId,
                                             @NotNull(message = "contributorRoleId는 필수 입력 항목입니다")
                                             List<@Positive(message = "contributorRoleId는 양수여야 합니다") Long> contributorRoleId) {
}
