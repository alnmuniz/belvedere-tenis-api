package br.com.belvedere.tenisapi.controller;

import br.com.belvedere.tenisapi.dto.UserDTO;
import br.com.belvedere.tenisapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // Protege todos os métodos deste controller
public class AdminUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{id}/promote")
    public ResponseEntity<UserDTO> promoteUser(@PathVariable("id") Long userId, Authentication authentication) {
        String adminAuthProviderId = authentication.getName();
        UserDTO updatedUser = userService.promoteUserToAdmin(userId,adminAuthProviderId);
        return ResponseEntity.ok(updatedUser);
    }
}