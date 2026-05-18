        package com.ws101.abundopolo.ecommerceapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
            csrfRequestHandler.setCsrfRequestAttributeName(null);

            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/landing.html", "/login.html", "/signup.html", "/products.html", "/detail.html", "/cart.html", "/checkout.html", "/account.html", "/script.js", "/css/**", "/js/**", "/images/**", "/pages/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").authenticated()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login.html")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/account.html", true)
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll()
                    )
                    .exceptionHandling(exception -> exception
                            .defaultAuthenticationEntryPointFor(
                                    (request, response, authException) -> response.sendError(401),
                                    request -> request.getRequestURI().startsWith("/api/")
                            )
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .httpBasic(AbstractHttpConfigurer::disable);

            return http.build();
    }
}
