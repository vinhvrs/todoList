package com.todoList.todoList.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig{

    
    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/");
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
                // .requestMatchers(
                //     "/", "/home", "/login", "/register", "/calendar_view", 
                //     "/user/**", "/static/**", "/css/**", "/js/**", "/api/user/**",
                //     "/api/**", "/api/**/**/**", "/api/**/**/**/**", "api/**/**"
                // ).permitAll()
                // .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")     // custom login page
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
        return http.build();
    }
}