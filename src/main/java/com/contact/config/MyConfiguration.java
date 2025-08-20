package com.contact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class MyConfiguration {

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceImpl(); // Ensure UserDetailsServiceImpl exists
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(this.getUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Substitute for configure(AuthenticationManagerBuilder auth)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        // This replaces configure(AuthenticationManagerBuilder auth)
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        // Explicitly register the authentication provider
        authenticationManager = new ProviderManager(authenticationProvider());
        return authenticationManager;
    }

    // Substitute for configure(HttpSecurity http)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/**").permitAll())
                .formLogin(form -> form
                        .loginPage("/signin") // ❌ REMOVE THIS LINE
                        .loginProcessingUrl("/do_login")
                        .defaultSuccessUrl("/user/dash", true) // true forces redirect always
                        // .failureUrl("/signin_fail")
                        .permitAll())
                .csrf().disable();

        return http.build();
    }

}