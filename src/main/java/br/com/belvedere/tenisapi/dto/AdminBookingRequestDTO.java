package br.com.belvedere.tenisapi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
public class AdminBookingRequestDTO {

    @NotNull(message = "O ID do morador é obrigatório")
    private Long targetUserId; // O ID do morador para quem a reserva será feita

    @NotNull(message = "A data e hora de início são obrigatórias")
    @Future(message = "A data da reserva deve ser no futuro")
    private Instant startTime;
}