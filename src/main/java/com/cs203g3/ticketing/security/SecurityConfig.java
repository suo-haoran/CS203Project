package com.cs203g3.ticketing.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setAllowedMethods(Arrays.asList("*"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/v1/concerts\\?showAll.*")).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/v1/concerts").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/v1/concerts/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/v1/concerts/*/categories/*/activeBallots").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/v1/concerts/*/categories/*/activeBallots").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/v1/concerts/*/categories/*/ballots").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/v1/concerts/*/categories/*/ballots/randomise").hasRole("ADMIN")

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
                    .requestMatchers("/css/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/concerts/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/payment-completed").permitAll()                    
                    .anyRequest().authenticated()
            );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
