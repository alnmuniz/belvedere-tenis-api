package br.com.belvedere.tenisapi.service;

import br.com.belvedere.tenisapi.dto.BlockBookingRequestDTO;
import br.com.belvedere.tenisapi.dto.BookingRequestDTO;
import br.com.belvedere.tenisapi.dto.BookingResponseDTO;
import br.com.belvedere.tenisapi.dto.UserResponseDTO;
import br.com.belvedere.tenisapi.entity.Booking;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.repository.BookingRepository;
import br.com.belvedere.tenisapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    // Fuso horário do condomínio (São Paulo)
    private static final ZoneId CONDOMINIUM_TIMEZONE = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true) // Boa prática para métodos de consulta
    public List<BookingResponseDTO> findBookingsForDate(LocalDate date) {
        // Converte a data local para o fuso horário do condomínio (São Paulo)
        // Isso garante que a busca seja feita considerando o horário local do condomínio
        Instant startOfDay = date.atStartOfDay().atZone(CONDOMINIUM_TIMEZONE).toInstant();
        Instant endOfDay = date.atTime(LocalTime.MAX).atZone(CONDOMINIUM_TIMEZONE).toInstant();
        List<Booking> bookings = bookingRepository.findBookingsForDay(startOfDay, endOfDay);

        // Converte a lista de Entidades para uma lista de DTOs
        return bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Método privado para fazer a conversão
    private BookingResponseDTO convertToDto(Booking booking) {
        UserResponseDTO userDto = new UserResponseDTO();
        // Aqui a mágica acontece! Ao chamar booking.getUser().getName(),
        // o Hibernate "acorda" o proxy e busca os dados do usuário.
        userDto.setName(booking.getUser().getName());
        userDto.setApartment(booking.getUser().getApartment());

        BookingResponseDTO bookingDto = new BookingResponseDTO();
        bookingDto.setId(booking.getId());
        bookingDto.setStartTime(booking.getStartTime());
        bookingDto.setEndTime(booking.getEndTime());
        bookingDto.setBookingType(booking.getBookingType());
        bookingDto.setPrimeTime(booking.isPrimeTime());
        bookingDto.setUser(userDto);

        return bookingDto;
    }


    @Transactional // Essencial para operações de escrita no banco
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO, String authProviderId) {
        // Encontra o usuário pelo ID do Auth0
        User user = userRepository.findByAuthProviderId(authProviderId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. REGRA: Verifica se o usuário já possui uma reserva ativa no futuro
        bookingRepository.findFirstFutureBookingByUserId(user.getId(), Instant.now())
                .ifPresent(b -> {
                    throw new RuntimeException("Usuário já possui uma reserva ativa.");
                });

        // 3. Define o tempo de fim e verifica se o horário está vago
        Instant endTime = requestDTO.getStartTime().plusSeconds(3600); // 1 hora = 3600 segundos
        if (bookingRepository.existsOverlappingBooking(requestDTO.getStartTime(), endTime)) {
            throw new RuntimeException("Horário já reservado.");
        }

        // 4. Cria e salva a entidade Booking
        Booking newBooking = new Booking();
        newBooking.setUser(user);
        newBooking.setStartTime(requestDTO.getStartTime());
        newBooking.setEndTime(endTime);
        newBooking.setBookingType("JOGO");
        newBooking.setStatus("CONFIRMED");
        newBooking.setPrimeTime(isPrimeTime(requestDTO.getStartTime())); // Lógica do horário nobre

        Booking savedBooking = bookingRepository.save(newBooking);

        // 5. Converte a entidade salva para DTO e retorna
        return convertToDto(savedBooking);
    }

    // Método auxiliar para a regra do horário nobre
    private boolean isPrimeTime(Instant startTime) {
        // Converte o Instant para o fuso horário do condomínio (São Paulo)
        // Isso garante que a verificação de horário nobre seja feita no horário local
        ZonedDateTime zonedDateTime = startTime.atZone(CONDOMINIUM_TIMEZONE);
        int hour = zonedDateTime.getHour();
        // Manhã (6h, 7h, 8h) ou Noite (18h, 19h, 20h) - horário de São Paulo
        return (hour >= 6 && hour < 9) || (hour >= 18 && hour < 21);
    }


    @Transactional
    public void cancelBooking(Long bookingId, String authProviderId) {

        User user = userRepository.findByAuthProviderId(authProviderId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // 1. Encontra a reserva
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Reserva com ID " + bookingId + " não encontrada."));

        // 2. Valida a permissão do usuário
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado. Você não tem permissão para cancelar esta reserva.");
        }

        // 3. NOVA REGRA: Verifica se o horário de início da reserva já passou.
        // Instant.now() é sempre UTC, assim como nosso startTime.
        if (booking.getStartTime().isBefore(Instant.now())) {
            throw new RuntimeException("Não é possível cancelar uma reserva que já começou ou está no passado.");
        }

        // 4. Deleta a reserva
        bookingRepository.delete(booking);
    }

    
    @Transactional
    public BookingResponseDTO blockCourtForClass(BlockBookingRequestDTO requestDTO, String adminAuthProviderId) {
        // Valida se o admin existe
        User adminUser = userRepository.findByAuthProviderId(adminAuthProviderId)
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));

        // Verifica se o horário está vago
        if (bookingRepository.existsOverlappingBooking(requestDTO.getStartTime(), requestDTO.getEndTime())) {
            throw new RuntimeException("O período solicitado conflita com uma reserva existente.");
        }

        // Cria e salva a entidade Booking com o tipo "AULA"
        Booking newBlock = new Booking();
        newBlock.setUser(adminUser); // A reserva é "do" admin que a criou
        newBlock.setStartTime(requestDTO.getStartTime());
        newBlock.setEndTime(requestDTO.getEndTime());
        newBlock.setBookingType("AULA");
        newBlock.setStatus("CONFIRMED");
        newBlock.setClassDetails(requestDTO.getClassDetails());
        newBlock.setPrimeTime(isPrimeTime(requestDTO.getStartTime()));

        Booking savedBlock = bookingRepository.save(newBlock);
        return convertToDto(savedBlock);
    }
}