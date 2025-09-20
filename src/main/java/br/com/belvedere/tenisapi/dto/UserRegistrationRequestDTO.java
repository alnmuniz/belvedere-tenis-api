package br.com.belvedere.tenisapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserRegistrationRequestDTO {
    @NotEmpty
    private String invitationToken;
    @NotEmpty
    private String name; // Nome que o usuário pode preencher na tela
    @NotEmpty
    private String authProviderId; // O 'sub' do token JWT do Auth0
}

// Exemplo de JSON para UserRegistrationRequestDTO para usar no Postman:
/*
{
  "invitationToken": "5a07467e-623a-4134-902a-04b485754629",
  "name": "João da Silva",
  "authProviderId": "auth0|654321abcdef"
}
*/