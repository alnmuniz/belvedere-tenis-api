package br.com.belvedere.tenisapi.controller;

import br.com.belvedere.tenisapi.dto.InvitationDetailsDTO;
import br.com.belvedere.tenisapi.dto.UserDTO;
import br.com.belvedere.tenisapi.dto.UserRegistrationRequestDTO;
import br.com.belvedere.tenisapi.service.InvitationService;
import br.com.belvedere.tenisapi.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserService userService;    

    @GetMapping("/invitations/{token}/validate")
    public ResponseEntity<InvitationDetailsDTO> validateInvitationToken(@PathVariable String token) {
        InvitationDetailsDTO details = invitationService.validateToken(token);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerUser(@Valid @RequestBody UserRegistrationRequestDTO requestDTO) {
        return userService.registerNewUser(requestDTO);
    }
}