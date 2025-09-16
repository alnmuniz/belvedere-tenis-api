package br.com.belvedere.tenisapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Value("${auth0.audience}") // Injeta o 'audience' do application.yaml
    private String audience;

    @Value("${app.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> {
                // 2. PERMITE AS REQUISIÇÕES PREFLIGHT (OPTIONS)
                authz.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                // Lê as rotas do application.yaml e as libera
                authz.requestMatchers(HttpMethod.GET, securityProperties.getPublicGetRoutes().toArray(new String[0])).permitAll();
                authz.requestMatchers(HttpMethod.POST, securityProperties.getPublicPostRoutes().toArray(new String[0])).permitAll();
                authz.requestMatchers(HttpMethod.DELETE, securityProperties.getPublicDeleteRoutes().toArray(new String[0])).permitAll();
                // Todas as outras rotas exigem autenticação
                authz.anyRequest().authenticated();
            })
            // ATIVA A VALIDAÇÃO DE TOKEN JWT:
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
            //.formLogin(AbstractHttpConfigurer::disable)
            //.httpBasic(AbstractHttpConfigurer::disable)
            ;

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        // 1. Cria um validador para a 'audience'
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtTimestampValidator();
        
        // 2. Cria um validador para o 'issuer', usando o valor do application.yaml
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        
        // 3. Combina os validadores
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        // 4. Configura o decoder para usar os validadores
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);
        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    // 3. DEFINE AS REGRAS DE CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // <<< MUDANÇA 2: Use a lista injetada em vez de uma lista hardcoded
        configuration.setAllowedOrigins(securityProperties.getCorsAllowedOrigins());
        // Permite os métodos HTTP que usamos
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
        // Permite cabeçalhos importantes, incluindo o de Autorização
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração para todas as rotas da nossa API
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Remove o prefixo padrão "SCOPE_"
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // Diz ao Spring para procurar as roles na nossa claim customizada
        grantedAuthoritiesConverter.setAuthoritiesClaimName("https://belvedere.tenis.api/roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}