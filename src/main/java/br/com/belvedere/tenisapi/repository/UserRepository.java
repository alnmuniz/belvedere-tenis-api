package br.com.belvedere.tenisapi.repository;

import br.com.belvedere.tenisapi.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuthProviderId(String authProviderId);
    Optional<User> findByEmail(String email);
}