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

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity 
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity { 

    private final ApplicationContext applicationContext;

    public SecurityConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configure Vaadin security ONLY
        setLoginView(http, LoginView.class);
        super.configure(http);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(
            "/register/**",
            "/register",
            "/test-register/**", 
            "/test-register",
            "/h2-console/**",
            "/images/**",
            "/icons/**",
            "/VAADIN/**",
            "/vaadinServlet/**",
            "/frontend/**",
            "/webjars/**",
            "/frontend-es5/**",
            "/frontend-es6/**"
        );
    }
}