package shop.nuribooks.books.common.converter;

import org.springframework.core.convert.converter.Converter;

import shop.nuribooks.books.book.point.enums.HistoryType;

public class HistoryTypeConverter implements Converter<String, HistoryType> {
	/**
	 * history type enum 변환하는 converter
	 * @param s
	 * @return
	 */
	@Override
	public HistoryType convert(String s) {
		return HistoryType.convert(s);
	}
}
