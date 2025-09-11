package br.com.belvedere.tenisapi.service;

import br.com.belvedere.tenisapi.dto.BookingResponseDTO;
import br.com.belvedere.tenisapi.dto.UserResponseDTO;
import br.com.belvedere.tenisapi.entity.Booking;
import br.com.belvedere.tenisapi.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional(readOnly = true) // Boa prática para métodos de consulta
    public List<BookingResponseDTO> findBookingsForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
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
}