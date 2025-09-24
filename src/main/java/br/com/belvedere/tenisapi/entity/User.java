package br.com.belvedere.tenisapi.entity;

import br.com.belvedere.tenisapi.enums.UserRole;
import br.com.belvedere.tenisapi.enums.UserStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
}