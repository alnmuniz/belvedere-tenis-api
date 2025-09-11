package br.com.belvedere.tenisapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilita o CSRF, pois nossa API será stateless (não usará sessões/cookies)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Define as regras de autorização para as requisições
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Permite que qualquer um (permitAll) acesse o endpoint GET /api/bookings
                .requestMatchers(HttpMethod.GET, "/api/bookings/**").permitAll() // Permite GET para buscar
                .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()  // Permite POST para criar
                // Exige que qualquer outra requisição (anyRequest) seja autenticada
                .anyRequest().authenticated()
            )

            // 3. Desabilita o formulário de login e o HTTP Basic, pois não vamos usá-los
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}