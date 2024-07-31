package _4.TourismContest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        // Adding both JSON and XML converters
        messageConverters.add(new MappingJackson2HttpMessageConverter());  // JSON converter
        messageConverters.add(new Jaxb2RootElementHttpMessageConverter()); // XML converter
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }
}