package shop.nuribooks.books.common.converter;

import org.springframework.core.convert.converter.Converter;

import shop.nuribooks.books.book.elasticsearch.enums.SearchType;

public class SearchTypeConverter implements Converter<String, SearchType> {
	/**
	 *
	 * @param s
	 * @return
	 */
	@Override
	public SearchType convert(String s) {
		return SearchType.convert(s);
	}
}
