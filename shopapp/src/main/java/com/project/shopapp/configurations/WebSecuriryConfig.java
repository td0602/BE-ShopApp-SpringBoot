package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecuriryConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                   request
                           .requestMatchers(
                                   String.format("%s/users/register", apiPrefix),
                                   String.format("%s/users/login", apiPrefix)
                           )
                           .permitAll()
//
                           .requestMatchers(HttpMethod.POST,
                                   String.format("%s/orders/**", apiPrefix)).hasRole("USER")
//                           voi request PUT phai co role la ADMIN
                           .requestMatchers(HttpMethod.PUT,
                                   String.format("%s/orders/**", apiPrefix)).hasAnyRole("USER", "ADMIN")
                           .requestMatchers(HttpMethod.GET,
                                   String.format("%s/orders/**", apiPrefix)).hasRole("USER")
                           .requestMatchers(HttpMethod.DELETE,
                                   String.format("%s/orders/**", apiPrefix)).hasRole("ADMIN")
                           .anyRequest().authenticated();
                });
        return httpSecurity.build();
    }
}
