package br.com.belvedere.tenisapi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingType;
    private boolean isPrimeTime;
    private UserResponseDTO user; // Usamos nosso DTO de usuário aqui
}