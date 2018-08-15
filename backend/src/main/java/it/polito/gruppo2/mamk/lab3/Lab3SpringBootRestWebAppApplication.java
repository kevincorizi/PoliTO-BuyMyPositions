package it.polito.gruppo2.mamk.lab3;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication
public class Lab3SpringBootRestWebAppApplication {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter () {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    public static void main(String[] args) {
        SpringApplication.run(Lab3SpringBootRestWebAppApplication.class, args);
    }

    @Configuration
    public static class WebappConfig implements WebMvcConfigurer {

        @Override
        public void configureMessageConverters(java.util.List<HttpMessageConverter<?>> converters)  {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.serializerByType(String.class, new ToStringSerializer());
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());
            converters.add(converter);
        }

        @Override
        public void addViewControllers(final ViewControllerRegistry registry) {
            // Map "/"
            registry.addViewController("/")
                    .setViewName("forward:/index.html");

            // Map "/word", "/word/word", and "/word/word/word" - except for anything starting with "/api/..." or ending with
            // a file extension like ".js" - to index.html. By doing this, the client receives and routes the url. It also
            // allows client-side URLs to be bookmarked.

            // Single directory level - no need to exclude "api"
            registry.addViewController("/{x:^(?!oauth|prova$)[\\w\\-]+$}")
                    .setViewName("forward:/index.html");
            // Multi-level directory path, need to exclude "api" on the first part of the path
            registry.addViewController("/{x:^(?!api|oauth$).*$}/**/{y:[\\w\\-]+}")
                    .setViewName("forward:/index.html");
        }
    }
}

