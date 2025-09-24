package br.com.belvedere.tenisapi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
public class BlockBookingRequestDTO {

    @NotNull
    @Future
    private Instant startTime;

    @NotNull
    @Future
    private Instant endTime;

    @NotEmpty
    private String classDetails; // Ex: "Aula Adulto - Iniciante"
}