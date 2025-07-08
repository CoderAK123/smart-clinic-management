package com.smartclinic.back_end.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

   @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/",
    "/login",
    "/api/**",
    "/pages/**",
    "/static/**",
    "/js/**",
    "/css/**",
    "/assets/**",
    "/images/**",
    "/adminDashboard/**",   // ✅ MUST BE HERE
    "/doctorDashboard/**",  // ✅ Also include if needed
    "/defineRole.html"
            ).permitAll()
            .anyRequest().authenticated()
        ) .httpBasic(httpBasic -> {});
        /* .formLogin(form -> form
            .loginPage("/login") // loads templates/login.html
            .permitAll()
        );*/
    return http.build();
}

}
