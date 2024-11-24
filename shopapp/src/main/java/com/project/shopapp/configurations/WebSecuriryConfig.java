package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
//@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
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
                           .requestMatchers(HttpMethod.GET,
                                   String.format("%s/roles**", apiPrefix)).permitAll()
                           .requestMatchers(HttpMethod.GET,
                                   String.format("%s/categories**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                           .requestMatchers(HttpMethod.POST,
                                   String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                           .requestMatchers(HttpMethod.PUT,
                                   String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                           .requestMatchers(HttpMethod.DELETE,
                                   String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)

                           .requestMatchers(HttpMethod.GET,
                                   String.format("%s/products**", apiPrefix)).permitAll()
                           .requestMatchers(HttpMethod.POST,
                                   String.format("%s/products**", apiPrefix)).hasRole(Role.ADMIN)
                           .requestMatchers(HttpMethod.POST,
                                   String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                           .requestMatchers(HttpMethod.PUT,
                                   String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                           .requestMatchers(HttpMethod.DELETE,
                                   String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                           . requestMatchers(HttpMethod.GET,
                                   String.format("%s/products/images/**", apiPrefix)).permitAll()
                           . requestMatchers(HttpMethod.GET,
//                                   CHÚ Ý: %s/products** không bao hàm cả %s/products/**
                                   String.format("%s/products/**", apiPrefix)).permitAll()
                           . requestMatchers(HttpMethod.GET,
                                   String.format("%s/products/by-ids**", apiPrefix)).permitAll()

                           .requestMatchers(HttpMethod.POST,
                                   String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)
//                           .requestMatchers(HttpMethod.GET,
//                                   String.format("%s/orders/get-orders-by-keyword", apiPrefix)).hasRole(Role.ADMIN)

                           .requestMatchers(HttpMethod.GET,
                                   String.format("%s/orders/**", apiPrefix)).permitAll()
//                           voi request PUT phai co role la ADMIN
                           .requestMatchers(HttpMethod.PUT,
                                   String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                           .requestMatchers(HttpMethod.GET,
                                   String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                           .requestMatchers(HttpMethod.DELETE,
                                   String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)

                           .requestMatchers(HttpMethod.POST,
                            String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)
//                           voi request PUT phai co role la ADMIN
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)

                            .anyRequest().authenticated(); // request khác cần xác thực
                }).csrf(AbstractHttpConfigurer::disable);

//        cau hinh server cho pheo thang client nao gui den day
        httpSecurity.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*")); // cho phep ai gui cung dc
                configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return httpSecurity.build();
    }
}
