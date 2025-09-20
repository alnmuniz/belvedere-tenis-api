package br.com.belvedere.tenisapi.dto;

import br.com.belvedere.tenisapi.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String apartment;
    private UserRole role;
}