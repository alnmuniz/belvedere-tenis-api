package br.com.belvedere.tenisapi.entity;

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

    private String role;

    @Column(name = "auth_provider_id", nullable = false, unique = true)
    private String authProviderId;
}