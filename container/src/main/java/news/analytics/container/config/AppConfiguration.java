package news.analytics.container.config;

import news.analytics.container.core.SolrClient;
import news.analytics.container.core.TrendGenerator;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class AppConfiguration {
    private DataSource dataSource;
    private Properties properties;

    public AppConfiguration() throws IOException {
        properties = loadProperties("dataSource.properties");
        dataSource = H2DataSource.getDataSource(properties.getProperty("driverClass"), properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPassword"));
    }

    @Bean
    public TrendGenerator trendGenerator() {
        return new TrendGenerator(dataSource);
    }

    @Bean
    public SolrClient solrClient(){
        return new SolrClient(properties.getProperty("solrServerUrl"));
    }

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

    private Properties loadProperties(String propertiesFileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found !");
        }
        return properties;
    }
}
