package br.com.belvedere.tenisapi.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class BookingResponseDTO {
    private Long id;
    private Instant startTime;
    private Instant endTime;
    private String bookingType;
    private boolean isPrimeTime;
    private UserResponseDTO user; // Usamos nosso DTO de usuário aqui
}