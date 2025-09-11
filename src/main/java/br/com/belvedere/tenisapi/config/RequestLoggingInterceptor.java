package br.com.belvedere.tenisapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Log de debug para verificar se o interceptor está sendo chamado
        System.out.println("🚀 INTERCEPTOR CHAMADO - Método: " + request.getMethod() + " - URI: " + request.getRequestURI());
        
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // Debug: verifica se o método está sendo chamado
        logger.info("🔍 INTERCEPTOR ATIVO - IP detectado: {}", clientIp);
        
        // Monta a URL completa se houver query parameters
        String fullUrl = uri;
        if (queryString != null && !queryString.isEmpty()) {
            fullUrl += "?" + queryString;
        }
        
        logger.info("=== REQUISIÇÃO RECEBIDA ===");
        logger.info("Timestamp: {}", timestamp);
        logger.info("Método: {}", method);
        logger.info("URL: {}", fullUrl);
        logger.info("IP do Cliente: {}", clientIp);
        logger.info("User-Agent: {}", userAgent);
        logger.info("==========================");
        
        return true; // Continua o processamento da requisição
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int statusCode = response.getStatus();
        
        logger.info("=== RESPOSTA ENVIADA ===");
        logger.info("Timestamp: {}", timestamp);
        logger.info("Método: {}", method);
        logger.info("URL: {}", uri);
        logger.info("Status: {}", statusCode);
        if (ex != null) {
            logger.info("Erro: {}", ex.getMessage());
        }
        logger.info("========================");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = null;
        
        // 1. Verifica X-Forwarded-For (para proxies/load balancers)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            ip = xForwardedFor.split(",")[0].trim();
        }
        
        // 2. Verifica X-Real-IP (para nginx)
        if (ip == null || ip.isEmpty()) {
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                ip = xRealIp;
            }
        }
        
        // 3. Usa o IP direto da requisição
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        
        // 4. Converte para formato IPv4 se necessário
        return formatToIPv4(ip);
    }
    
    private String formatToIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }
        
        // Remove espaços em branco
        ip = ip.trim();
        
        // Se for IPv6 localhost, converte para IPv4 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        
        // Se for IPv6 mapeado para IPv4 (::ffff:192.168.1.1), extrai o IPv4
        if (ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }
        
        // Se já for IPv4, retorna como está
        if (isIPv4(ip)) {
            return ip;
        }
        
        // Se for IPv6 puro, tenta converter para IPv4 se possível
        if (isIPv6(ip)) {
            // Para IPv6 localhost, retorna 127.0.0.1
            if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
                return "127.0.0.1";
            }
            // Para outros IPv6, mantém o formato original mas adiciona nota
            return ip + " (IPv6)";
        }
        
        // Se não conseguir identificar, retorna como está
        return ip;
    }
    
    private boolean isIPv4(String ip) {
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return false;
            
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isIPv6(String ip) {
        return ip.contains(":") && !ip.contains(".");
    }
}
