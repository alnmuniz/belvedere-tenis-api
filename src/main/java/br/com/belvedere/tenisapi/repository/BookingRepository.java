package br.com.belvedere.tenisapi.repository;

import br.com.belvedere.tenisapi.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // O Spring Data JPA é inteligente. Ele entende o nome do método!
    // Mas para clareza, vamos usar a anotação @Query com JPQL.
    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startOfDay AND b.startTime < :endOfDay ORDER BY b.startTime")
    List<Booking> findBookingsForDay(LocalDateTime startOfDay, LocalDateTime endOfDay);
}