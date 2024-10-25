package shop.nuribooks.books.book.bookstate.entitiy;

public enum BookStateEnum {
	SOLDOUT("매진"),
	INSTOCK("재고있음"),
	OUTOFPRINT("절판"),
	DISCONTINUED("단종"),
	PREORDER("예약판매");

	private String korName;

	BookStateEnum(String korName){
		this.korName = korName;
	}

	@Override
	public String toString(){
		return korName;
	}
}
