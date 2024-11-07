package shop.nuribooks.books.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.nuribooks.books.common.filter.MemberIdFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MemberIdFilter> loggingFilter(){
        FilterRegistrationBean<MemberIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MemberIdFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
