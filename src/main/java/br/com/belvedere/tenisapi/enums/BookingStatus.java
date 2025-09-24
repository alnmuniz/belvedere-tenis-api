package br.com.belvedere.tenisapi.enums;

/**
 * Enum que representa os status possíveis de uma reserva
 */
public enum BookingStatus {
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED"),
    PENDING("PENDING");
    
    private final String value;
    
    BookingStatus(String value) {
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
    public static BookingStatus fromValue(String value) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de reserva inválido: " + value);
    }
}
