package org.dasher.speed.taskmanagement.security;

import org.dasher.speed.taskmanagement.ui.view.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity 
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity { 

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configura a página de login
        setLoginView(http, LoginView.class);
        
        // Configura o Vaadin security
        super.configure(http);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Ignora completamente essas URLs do sistema de segurança
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        UserDetails user =
                User.withUsername("usuario@email.com")
                        .password("{noop}user")
                        .roles("USER")
                        .build();
        UserDetails admin =
                User.withUsername("admin@email.com")
                        .password("{noop}admin")
                        .roles("ADMIN")
                        .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}