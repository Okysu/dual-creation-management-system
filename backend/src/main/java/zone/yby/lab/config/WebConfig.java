package zone.yby.lab.config;

import database.mapper.LogMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zone.yby.lab.filter.RepeatedlyReadFilter;
import zone.yby.lab.handler.GlobalRequestInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LogMapper logMapper;

    public WebConfig(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Bean
    public FilterRegistrationBean<RepeatedlyReadFilter> repeatedlyReadFilter() {
        FilterRegistrationBean<RepeatedlyReadFilter> registration = new FilterRegistrationBean<>();
        RepeatedlyReadFilter repeatedlyReadFilter = new RepeatedlyReadFilter();
        registration.setFilter(repeatedlyReadFilter);
        return registration;
    }

    /**
     * 添加日志拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalRequestInterceptor(logMapper));
    }
}
