package br.com.belvedere.tenisapi.enums;

/**
 * Enum que representa os status possíveis de um convite
 */
public enum InvitationStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    EXPIRED("EXPIRED");
    
    private final String value;
    
    InvitationStatus(String value) {
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
    public static InvitationStatus fromValue(String value) {
        for (InvitationStatus status : InvitationStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de convite inválido: " + value);
    }
}
