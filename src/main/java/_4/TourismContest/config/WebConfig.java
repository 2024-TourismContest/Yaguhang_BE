package _4.TourismContest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:application.yml")
public class WebConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    private final CorsProperties corsProperties;

    @Autowired
    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 사용자 프로필 이미지 경로 설정
        registry.addResourceHandler("/profileImages/**")
                .addResourceLocations("file:/home/mschoi/Desktop/tourismContest/String-BE/profileImages/");

        // 구단 로고 경로 설정
        registry.addResourceHandler("/teamLogos/**")
                .addResourceLocations("file:/home/mschoi/Desktop/tourismContest/String-BE/teamLogos/");
        // 경기장 경로 설정
        registry.addResourceHandler("/stadiums/**")
                .addResourceLocations("file:/home/mschoi/Desktop/tourismContest/String-BE/stadiumImgs/");
    }
}
