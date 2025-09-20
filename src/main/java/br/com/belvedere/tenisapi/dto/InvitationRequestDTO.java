package br.com.belvedere.tenisapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class InvitationRequestDTO {
    @NotEmpty @Email
    private String email;
    @NotEmpty
    private String apartment;
}