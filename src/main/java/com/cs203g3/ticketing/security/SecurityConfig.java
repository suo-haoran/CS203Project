package com.cs203g3.ticketing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cs203g3.ticketing.security.auth.UserDetailsServiceImpl;
import com.cs203g3.ticketing.security.jwt.AuthEntryPointJwt;
import com.cs203g3.ticketing.security.jwt.AuthTokenFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;


    @Bean
    AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder());
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers(HttpMethod.POST, "/v1/concerts").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/v1/concerts/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/v1/concerts/*/categories/*/activeBallots").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*/categories/*/activeBallots").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/v1/concerts/*/categories/*/ballots").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/v1/concerts/*/categories/*/prices").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/v1/concerts/*/categories/*/prices").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*/categories/*/prices").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/v1/concerts/*/images").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*/images").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/v1/concerts/*/sessions").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/v1/concerts/*/sessions/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*/sessions/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/v1/concerts/*/sessions/*/tickets").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/v1/receipts").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/v1/receipts/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/receipts/*").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/v1/tickets").hasRole("ADMIN")

                    .requestMatchers("/v1/auth/**").permitAll()
                    .requestMatchers("/v1/test/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/concerts/**").permitAll()
                    .anyRequest().authenticated()
            );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
