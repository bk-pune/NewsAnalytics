package news.analytics.container.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    // TODO Add authentication on requests
   /* @Bean
    public FilterRegistrationBean<AuthenticationFilter> loggingFilter(){
        FilterRegistrationBean<AuthenticationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthenticationFilter());
        registrationBean.addUrlPatterns("/protected/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }*/
}
