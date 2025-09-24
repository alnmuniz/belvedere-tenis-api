package br.com.belvedere.tenisapi.enums;

/**
 * Enum que representa os papéis/roles dos usuários no sistema
 */
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    
    private final String value;
    
    UserRole(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Converte uma string para o enum correspondente
     * @param value o valor da string
     * @return o enum correspondente
     * @throws IllegalArgumentException se o valor não for válido
     */
    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role inválida: " + value);
    }
}
