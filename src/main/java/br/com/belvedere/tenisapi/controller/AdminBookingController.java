package br.com.belvedere.tenisapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.belvedere.tenisapi.dto.AdminBookingRequestDTO;
import br.com.belvedere.tenisapi.dto.BlockBookingRequestDTO;
import br.com.belvedere.tenisapi.dto.BookingResponseDTO;
import br.com.belvedere.tenisapi.service.BookingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // <<< MÁGICA DA AUTORIZAÇÃO
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO blockCourt(@Valid @RequestBody BlockBookingRequestDTO requestDTO, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        return bookingService.blockCourtForClass(requestDTO, adminAuthProviderId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBookingAsAdmin(@PathVariable("id") Long bookingId, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        bookingService.adminCancelBooking(bookingId, adminAuthProviderId);
        // Retorna 204 No Content, indicando sucesso na deleção.
        return ResponseEntity.noContent().build();
    }

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBookingForUser(@Valid @RequestBody AdminBookingRequestDTO requestDTO, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        return bookingService.createBookingForUser(requestDTO, adminAuthProviderId);
    }
}