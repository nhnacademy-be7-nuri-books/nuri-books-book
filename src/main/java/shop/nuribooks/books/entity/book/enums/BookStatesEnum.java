package shop.nuribooks.books.entity.book.enums;

public enum BookStatesEnum {
	SOLDOUT("매진"),
	INSTOCK("재고있음"),
	OUTOFPRINT("절판"),
	DISCONTINUED("단종"),
	PREORDER("예약판매");

	private final String koreanName;

	BookStatesEnum(String koreanName){
		this.koreanName = koreanName;
	}

	@Override
	public String toString(){
		return koreanName + "(" + this.name() + ")";
	}
}
