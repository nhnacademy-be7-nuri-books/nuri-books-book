// package shop.nuribooks.books.book.point.entity.child;
//
// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.MapsId;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import shop.nuribooks.books.book.point.entity.PointHistory;
//
// @Entity
// @Getter
// @NoArgsConstructor
// @Table(name = "refund_returning_point")
// public class RefundReturningPoint {
// 	@Id
// 	private Long id;
//
// 	@OneToOne
// 	@MapsId
// 	@JoinColumn(name = "point_history_id")
// 	private PointHistory pointHistory;
//
// 	// TODO:: 환불 id 추가
// }
