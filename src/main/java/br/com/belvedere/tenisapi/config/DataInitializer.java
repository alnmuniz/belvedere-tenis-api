package br.com.belvedere.tenisapi.config;

import br.com.belvedere.tenisapi.entity.Booking;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.enums.BookingStatus;
import br.com.belvedere.tenisapi.enums.BookingType;
import br.com.belvedere.tenisapi.enums.UserRole;
import br.com.belvedere.tenisapi.repository.BookingRepository;
import br.com.belvedere.tenisapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
        List<User> users = createUsers();
        userRepository.saveAll(users);
        log.info("Usuários de teste salvos: {}", users.size());

        // Cria Reservas variadas
        List<Booking> bookings = createBookings(users);
        bookingRepository.saveAll(bookings);
        log.info("Reservas de teste salvas: {}", bookings.size());
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        // Admin
        User admin = new User();
        admin.setName("Andre Muniz");
        admin.setEmail("alnmuniz@gmail.com");
        admin.setApartment("1102");
        admin.setRole(UserRole.ADMIN);
        admin.setAuthProviderId("google-oauth2|118232728984586307358");
        users.add(admin);

        // Usuários comuns
        User user1 = new User();
        user1.setName("Carlos Santana");
        user1.setEmail("carlos@email.com");
        user1.setApartment("101");
        user1.setRole(UserRole.USER);
        user1.setAuthProviderId("6mm8DvO7VaVFqJ8Sm5Kc3EUkPPsmIwCt@clients");
        users.add(user1);

        User user2 = new User();
        user2.setName("Maria Joaquina");
        user2.setEmail("maria@email.com");
        user2.setApartment("302");
        user2.setRole(UserRole.USER);
        user2.setAuthProviderId("google-67890");
        users.add(user2);

        User user3 = new User();
        user3.setName("João Silva");
        user3.setEmail("joao.silva@email.com");
        user3.setApartment("201");
        user3.setRole(UserRole.USER);
        user3.setAuthProviderId("auth0|user123456");
        users.add(user3);

        User user4 = new User();
        user4.setName("Ana Costa");
        user4.setEmail("ana.costa@email.com");
        user4.setApartment("402");
        user4.setRole(UserRole.USER);
        user4.setAuthProviderId("auth0|user789012");
        users.add(user4);

        User user5 = new User();
        user5.setName("Pedro Oliveira");
        user5.setEmail("pedro.oliveira@email.com");
        user5.setApartment("502");
        user5.setRole(UserRole.USER);
        user5.setAuthProviderId("auth0|user345678");
        users.add(user5);

        return users;
    }

    private List<Booking> createBookings(List<User> users) {
        List<Booking> bookings = new ArrayList<>();
        LocalDate today = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault();

        // Busca usuários específicos
        User admin = users.stream().filter(u -> u.getRole() == UserRole.ADMIN).findFirst().orElse(users.get(0));
        User user1 = users.stream().filter(u -> u.getName().equals("Carlos Santana")).findFirst().orElse(users.get(1));
        User user2 = users.stream().filter(u -> u.getName().equals("Maria Joaquina")).findFirst().orElse(users.get(2));
        User user3 = users.stream().filter(u -> u.getName().equals("João Silva")).findFirst().orElse(users.get(3));
        User user4 = users.stream().filter(u -> u.getName().equals("Ana Costa")).findFirst().orElse(users.get(4));
        User user5 = users.stream().filter(u -> u.getName().equals("Pedro Oliveira")).findFirst().orElse(users.get(5));

        // RESERVAS PASSADAS (últimos 3 dias)
        // Dia -3
        bookings.add(createBooking(user1, today.minusDays(3), 8, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user2, today.minusDays(3), 14, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Iniciante"));
        bookings.add(createBooking(user3, today.minusDays(3), 18, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));

        // Dia -2
        bookings.add(createBooking(user4, today.minusDays(2), 7, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user5, today.minusDays(2), 15, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Avançada"));
        bookings.add(createBooking(user1, today.minusDays(2), 19, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));

        // Dia -1
        bookings.add(createBooking(user2, today.minusDays(1), 9, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Intermediária"));
        bookings.add(createBooking(user3, today.minusDays(1), 16, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user4, today.minusDays(1), 20, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));

        // RESERVAS DE HOJE
        bookings.add(createBooking(user1, today, 8, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user2, today, 11, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(admin, today, 14, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Adulto - Iniciante"));
        bookings.add(createBooking(user3, today, 18, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user4, today, 19, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Kids"));

        // RESERVAS FUTURAS (próximos 3 dias)
        // Dia +1
        bookings.add(createBooking(user5, today.plusDays(1), 7, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user1, today.plusDays(1), 13, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Técnica"));
        bookings.add(createBooking(user2, today.plusDays(1), 17, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));

        // Dia +2
        bookings.add(createBooking(user3, today.plusDays(2), 8, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Condicionamento"));
        bookings.add(createBooking(user4, today.plusDays(2), 15, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user5, today.plusDays(2), 18, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));

        // Dia +3
        bookings.add(createBooking(user1, today.plusDays(3), 9, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));
        bookings.add(createBooking(user2, today.plusDays(3), 14, 0, BookingType.TURMA_COLETIVA, BookingStatus.CONFIRMED, zoneId, "Aula Dupla"));
        bookings.add(createBooking(user3, today.plusDays(3), 19, 0, BookingType.RESERVA, BookingStatus.CONFIRMED, zoneId));

        // Algumas reservas canceladas para variedade
        Booking cancelledBooking = createBooking(user4, today.plusDays(1), 10, 0, BookingType.RESERVA, BookingStatus.CANCELLED, zoneId);
        bookings.add(cancelledBooking);

        Booking pendingBooking = createBooking(user5, today.plusDays(2), 11, 0, BookingType.TURMA_COLETIVA, BookingStatus.PENDING, zoneId, "Aula Experimental");
        bookings.add(pendingBooking);

        return bookings;
    }

    private Booking createBooking(User user, LocalDate date, int hour, int minute, 
                                 BookingType type, BookingStatus status, ZoneId zoneId) {
        return createBooking(user, date, hour, minute, type, status, zoneId, null);
    }

    private Booking createBooking(User user, LocalDate date, int hour, int minute, 
                                 BookingType type, BookingStatus status, ZoneId zoneId, String classDetails) {
        Booking booking = new Booking();
        booking.setUser(user);
        
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        
        booking.setStartTime(startDateTime.atZone(zoneId).toInstant());
        booking.setEndTime(endDateTime.atZone(zoneId).toInstant());
        booking.setBookingType(type);
        booking.setStatus(status);
        booking.setPrimeTime(isPrimeTime(startDateTime));
        
        if (classDetails != null) {
            booking.setClassDetails(classDetails);
        }
        
        return booking;
    }

    private boolean isPrimeTime(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        return (hour >= 6 && hour < 9) || (hour >= 18 && hour < 21);
    }
}