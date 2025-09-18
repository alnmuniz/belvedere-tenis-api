package br.com.belvedere.tenisapi.entity;

import br.com.belvedere.tenisapi.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users", schema = "belvedere")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String apartment;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "auth_provider_id", nullable = false, unique = true)
    private String authProviderId;
}