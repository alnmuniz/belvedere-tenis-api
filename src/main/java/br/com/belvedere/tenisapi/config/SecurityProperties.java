package br.com.belvedere.tenisapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private List<String> corsAllowedOrigins = new ArrayList<>();
    private List<String> publicGetRoutes = new ArrayList<>();
    private List<String> publicPostRoutes = new ArrayList<>();
    private List<String> publicDeleteRoutes = new ArrayList<>();    
}