package org.dasher.speed.taskmanagement.security;

import org.dasher.speed.taskmanagement.ui.view.LoginView;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity 
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private final ApplicationContext applicationContext;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CsrfTokenRepository csrfTokenRepository;

    public SecurityConfiguration(
            ApplicationContext applicationContext,
            CorsConfigurationSource corsConfigurationSource,
            CsrfTokenRepository csrfTokenRepository) {
        this.applicationContext = applicationContext;
        this.corsConfigurationSource = corsConfigurationSource;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/actuator/**"),
                    new AntPathRequestMatcher("/instances/**"),
                    new AntPathRequestMatcher("/applications/**")
                )
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/instances/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/applications/**")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
            );

        // Configure Vaadin security (must be after the other rules)
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        
        web.ignoring().requestMatchers(
            // Static resources
            "/VAADIN/**",
            "/favicon.ico",
            "/robots.txt",
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/icons/**",
            "/images/**",
            "/frontend/**",
            "/webjars/**",
            "/h2-console/**",
            "/frontend-es5/**", 
            "/frontend-es6/**",
            
            // Register endpoints
            "/register/**",
            "/register",
            "/test-register/**",
            "/test-register",
            
            // Spring Boot Admin endpoints
            "/actuator/**",
            "/instances/**",
            "/applications/**"
        );
    }
}