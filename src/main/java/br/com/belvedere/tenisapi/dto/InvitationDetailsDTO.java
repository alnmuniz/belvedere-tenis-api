package br.com.belvedere.tenisapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Construtor útil para criar o objeto facilmente
public class InvitationDetailsDTO {
    private String email;
    private String apartment;
}