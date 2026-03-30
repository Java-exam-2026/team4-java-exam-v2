package com.javaexam.config;

import com.javaexam.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    
    @Value("${spring.security.remember-me.key}")
    private String rememberMeKey;
    
    @Value("${spring.security.remember-me.token-validity-seconds}")
    private int rememberMeTokenValiditySeconds;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/quiz/**").hasRole("USER")
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))
                .formLogin(form -> form
                .loginPage("/login")
                // ここから書き換え！
                .successHandler((request, response, authentication) -> {
                    // ログインした人の権限（バッジ）を確認
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (isAdmin) {
                    // 管理者なら、要件通り /admin/dashboard へ
                    response.sendRedirect("/admin/dashboard");
                } else {
                // 一般ユーザーなら、トップページ / へ
                    response.sendRedirect("/");
                }
                })
                .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(rememberMeTokenValiditySeconds));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
