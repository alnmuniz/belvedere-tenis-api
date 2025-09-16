package br.com.belvedere.tenisapi.config;

import br.com.belvedere.tenisapi.entity.Booking;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.repository.BookingRepository;
import br.com.belvedere.tenisapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Component
@Profile("dev") // Esta classe só será executada quando o perfil "dev" estiver ativo
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Carregando dados de teste...");

        // Cria Usuários
        User admin = new User();
        admin.setName("Andre Muniz");
        admin.setEmail("alnmuniz@gmail.com");
        admin.setApartment("1102");
        admin.setRole("ROLE_ADMIN");
        admin.setAuthProviderId("google-oauth2|118232728984586307358");

        User user1 = new User();
        user1.setName("Carlos Santana");
        user1.setEmail("carlos@email.com");
        user1.setApartment("101");
        user1.setRole("ROLE_USER");
        user1.setAuthProviderId("6mm8DvO7VaVFqJ8Sm5Kc3EUkPPsmIwCt@clients"); // ID Fictício do Provedor de Auth

        User user2 = new User();
        user2.setName("Maria Joaquina");
        user2.setEmail("maria@email.com");
        user2.setApartment("302");
        user2.setRole("ROLE_USER");
        user2.setAuthProviderId("google-67890"); // ID Fictício do Provedor de Auth

        userRepository.saveAll(Arrays.asList(admin, user1, user2));
        log.info("Usuários de teste salvos.");

        // Cria Reservas para a data de hoje (10/09/2025)
        Booking booking1 = new Booking();
        booking1.setUser(user1);
        // Horário nobre da manhã
        booking1.setStartTime(LocalDateTime.of(2025, 9, 10, 8, 0).atZone(ZoneId.systemDefault()).toInstant());
        booking1.setEndTime(LocalDateTime.of(2025, 9, 10, 9, 0).atZone(ZoneId.systemDefault()).toInstant());
        booking1.setBookingType("JOGO");
        booking1.setStatus("CONFIRMED");
        booking1.setPrimeTime(true);

        Booking booking2 = new Booking();
        booking2.setUser(user2);
        // Horário não nobre
        booking2.setStartTime(LocalDateTime.of(2025, 9, 10, 11, 0).atZone(ZoneId.systemDefault()).toInstant());
        booking2.setEndTime(LocalDateTime.of(2025, 9, 10, 12, 0).atZone(ZoneId.systemDefault()).toInstant());
        booking2.setBookingType("JOGO");
        booking2.setStatus("CONFIRMED");
        booking2.setPrimeTime(false);

        bookingRepository.saveAll(Arrays.asList(booking1, booking2));
        log.info("Reservas de teste salvas.");
    }
}