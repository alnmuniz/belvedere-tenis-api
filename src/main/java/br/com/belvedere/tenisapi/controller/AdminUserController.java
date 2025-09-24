package br.com.belvedere.tenisapi.controller;

import br.com.belvedere.tenisapi.dto.UserDTO;
import br.com.belvedere.tenisapi.service.InvitationService;
import br.com.belvedere.tenisapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import br.com.belvedere.tenisapi.dto.InvitationRequestDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // Protege todos os métodos deste controller
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;
    
    @PostMapping("/{id}/promote")
    public ResponseEntity<UserDTO> promoteUser(@PathVariable("id") Long userId, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        UserDTO updatedUser = userService.promoteUserToAdmin(userId,adminAuthProviderId);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PostMapping("/{id}/demote")
    public ResponseEntity<UserDTO> demoteUser(@PathVariable("id") Long userId, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        UserDTO updatedUser = userService.demoteUserToUser(userId, adminAuthProviderId);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204, pois não há conteúdo a retornar
    public void sendInvitation(@Valid @RequestBody InvitationRequestDTO requestDTO, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        invitationService.createAndSendInvitation(requestDTO.getEmail(), requestDTO.getApartment(), adminAuthProviderId);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<UserDTO> blockUser(@PathVariable("id") Long userId, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        UserDTO updatedUser = userService.blockUser(userId, adminAuthProviderId);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable("id") Long userId, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        UserDTO updatedUser = userService.unblockUser(userId, adminAuthProviderId);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Método para buscar todos os usuários
     * @return Lista de usuários
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}