package br.com.belvedere.tenisapi.enums;

/**
 * Enum que representa os tipos de reserva disponíveis
 */
public enum BookingType {
    RESERVA("RESERVA"),
    TURMA_COLETIVA("TURMA COLETIVA");
    
    private final String value;
    
    BookingType(String value) {
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
    public static BookingType fromValue(String value) {
        for (BookingType type : BookingType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de reserva inválido: " + value);
    }
}
