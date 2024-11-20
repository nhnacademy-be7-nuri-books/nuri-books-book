package shop.nuribooks.books.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import shop.nuribooks.books.common.converter.HistoryTypeConverter;
import shop.nuribooks.books.common.converter.SearchTypeConverter;
import shop.nuribooks.books.common.converter.SortTypeConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	/**
	 * converter 등록하는 configure
	 * @param registry
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		// History converter 추가
		registry.addConverter(new HistoryTypeConverter());
		registry.addConverter(new SearchTypeConverter());
		registry.addConverter(new SortTypeConverter());
	}
}
