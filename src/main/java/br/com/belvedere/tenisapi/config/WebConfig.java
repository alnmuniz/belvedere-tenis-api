package br.com.belvedere.tenisapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("Registrando RequestLoggingInterceptor...");
        
        // Registra o interceptor para todas as rotas da API
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**") // Intercepta todas as rotas
                .excludePathPatterns(
                        "/actuator/**", // Exclui endpoints do Actuator (health checks, etc.)
                        "/error"        // Exclui página de erro padrão
                );
        
        logger.info("RequestLoggingInterceptor registrado com sucesso!");
    }
}
