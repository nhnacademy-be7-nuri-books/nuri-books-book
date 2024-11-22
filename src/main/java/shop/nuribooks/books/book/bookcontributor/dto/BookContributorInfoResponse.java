package shop.nuribooks.books.book.bookcontributor.dto;

public record BookContributorInfoResponse(
	Long contributorId,
	String contributorName,
	Long contributorRoleId,
	String contributorRoleName) {

	public static BookContributorInfoResponse of(Long contributorId,
		String contributorName,
		Long contributorRoleId,
		String contributorRoleName) {
		return new BookContributorInfoResponse(contributorId, contributorName, contributorRoleId, contributorRoleName);
	}
}