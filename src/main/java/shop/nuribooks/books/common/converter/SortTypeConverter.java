package shop.nuribooks.books.common.converter;

import org.springframework.core.convert.converter.Converter;

import shop.nuribooks.books.book.elasticsearch.enums.SortType;

public class SortTypeConverter implements Converter<String, SortType> {
	/**
	 * history type enum 변환하는 converter
	 * @param s
	 * @return
	 */
	@Override
	public SortType convert(String s) {
		return SortType.convert(s);
	}
}
