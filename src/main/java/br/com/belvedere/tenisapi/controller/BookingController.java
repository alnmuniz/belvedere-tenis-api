package br.com.belvedere.tenisapi.controller;

import br.com.belvedere.tenisapi.dto.BookingResponseDTO;
import br.com.belvedere.tenisapi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.belvedere.tenisapi.dto.BookingRequestDTO; // Importe o DTO
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingResponseDTO> bookings = bookingService.findBookingsForDate(date);
        return ResponseEntity.ok(bookings);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna o status 201 Created em caso de sucesso
    public BookingResponseDTO createBooking(@Valid @RequestBody BookingRequestDTO requestDTO, Authentication authentication) {
        
        String authProviderId = authentication.getName();
        return bookingService.createBooking(requestDTO, authProviderId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable("id") Long bookingId,
            Authentication authentication) {

        String authProviderId = authentication.getName();
        bookingService.cancelBooking(bookingId, authProviderId);
        // Retorna 204 No Content, o status padrão para um DELETE bem-sucedido.
        return ResponseEntity.noContent().build();
    }
}