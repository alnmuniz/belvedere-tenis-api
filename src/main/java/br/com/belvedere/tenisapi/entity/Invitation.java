package br.com.belvedere.tenisapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "invitations", schema = "belvedere")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String apartment;

    @Column(nullable = false, unique = true)
    private String token;

    private String status;

    @Column(name = "expires_at")
    private Instant expiresAt;
}