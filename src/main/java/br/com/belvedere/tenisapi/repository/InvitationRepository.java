package br.com.belvedere.tenisapi.repository;

import br.com.belvedere.tenisapi.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    /**
     * Busca um convite pelo token único
     * @param token o token do convite
     * @return o convite encontrado ou Optional.empty()
     */
    Optional<Invitation> findByToken(String token);

    /**
     * Busca convites por email
     * @param email o email do convidado
     * @return lista de convites para o email
     */
    List<Invitation> findByEmail(String email);

    /**
     * Busca convites por status
     * @param status o status do convite
     * @return lista de convites com o status especificado
     */
    List<Invitation> findByStatus(String status);

    /**
     * Busca convites por apartamento
     * @param apartment o número do apartamento
     * @return lista de convites para o apartamento
     */
    List<Invitation> findByApartment(String apartment);

    /**
     * Busca convites que expiraram (data de expiração menor que o tempo atual)
     * @param currentTime tempo atual
     * @return lista de convites expirados
     */
    @Query("SELECT i FROM Invitation i WHERE i.expiresAt < :currentTime AND i.status != 'EXPIRED'")
    List<Invitation> findExpiredInvitations(@Param("currentTime") Instant currentTime);

    /**
     * Busca convites pendentes que não expiraram
     * @param currentTime tempo atual
     * @return lista de convites pendentes válidos
     */
    @Query("SELECT i FROM Invitation i WHERE i.status = 'PENDING' AND i.expiresAt > :currentTime")
    List<Invitation> findValidPendingInvitations(@Param("currentTime") Instant currentTime);

    /**
     * Verifica se existe um convite ativo (pendente e não expirado) para um email
     * @param email o email do convidado
     * @param currentTime tempo atual
     * @return true se existe convite ativo, false caso contrário
     */
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.email = :email AND i.status = 'PENDING' AND i.expiresAt > :currentTime")
    boolean existsActiveInvitationForEmail(@Param("email") String email, @Param("currentTime") Instant currentTime);

    /**
     * Busca convites por email e status
     * @param email o email do convidado
     * @param status o status do convite
     * @return lista de convites para o email com o status especificado
     */
    List<Invitation> findByEmailAndStatus(String email, String status);

    /**
     * Busca convites por apartamento e status
     * @param apartment o número do apartamento
     * @param status o status do convite
     * @return lista de convites para o apartamento com o status especificado
     */
    List<Invitation> findByApartmentAndStatus(String apartment, String status);
}
