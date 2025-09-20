package br.com.belvedere.tenisapi.service;

import br.com.belvedere.tenisapi.dto.UserDTO;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.enums.UserRole;
import br.com.belvedere.tenisapi.repository.UserRepository;
import br.com.belvedere.tenisapi.dto.UserRegistrationRequestDTO;
import br.com.belvedere.tenisapi.entity.Invitation;
import br.com.belvedere.tenisapi.enums.InvitationStatus;
import br.com.belvedere.tenisapi.service.InvitationService; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationService invitationService; 

    @Transactional
    public UserDTO promoteUserToAdmin(Long userId, String adminAuthProviderId) {

        // Valida se o admin existe
        User adminUser = userRepository.findByAuthProviderId(adminAuthProviderId)
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));


        // 1. Encontra o usuário no banco
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + userId + " não encontrado."));

        // 2. Altera o papel (role) para ADMIN
        user.setRole(UserRole.ADMIN);

        // 3. Salva a alteração
        User updatedUser = userRepository.save(user);

        // 5. Registra o log da operação administrativa
        logger.info("Usuário comum promovido a administrador - " +
        "Usuário promovido: {} (ID: {}, Apartamento: {}), " +
        "Admin responsável: {} (ID: {})",
        user.getName(),
        user.getId(),
        user.getApartment(),
        adminUser.getName(),
        adminUser.getId());

        // 4. Converte para DTO e retorna
        return convertToUserDTO(updatedUser);
    }

    // Método auxiliar para conversão
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setApartment(user.getApartment());
        dto.setRole(user.getRole());
        return dto;
    }

    @Transactional
    public UserDTO registerNewUser(UserRegistrationRequestDTO requestDTO) {
        // 1. Valida o token e busca o convite
        Invitation invitation = invitationService.findValidInvitationByToken(requestDTO.getInvitationToken());
        if (invitation == null || invitation.getStatus() != InvitationStatus.PENDING || invitation.getExpiresAt().isBefore(java.time.Instant.now())) {
            throw new RuntimeException("Token de convite não existe ou já expirou.");
        }

        // 2. Verifica se já não existe um usuário com o mesmo e-mail ou ID do provedor
        if (userRepository.findByEmail(invitation.getEmail()).isPresent() ||
            userRepository.findByAuthProviderId(requestDTO.getAuthProviderId()).isPresent()) {
            throw new RuntimeException("Usuário já cadastrado.");
        }

        // 3. Cria a nova entidade User
        User newUser = new User();
        newUser.setName(requestDTO.getName());
        newUser.setAuthProviderId(requestDTO.getAuthProviderId());
        newUser.setEmail(invitation.getEmail());
        newUser.setApartment(invitation.getApartment());
        newUser.setRole(UserRole.USER); // Novos usuários sempre são moradores comuns
        User savedUser = userRepository.save(newUser);

        // 4. Marca o convite como utilizado
        invitationService.markInvitationAsAccepted(invitation);

        // 5. Log de auditoria da operação de registro de novo usuário
        logger.info("Novo usuário registrado - Nome: {}, Email: {}, Apartamento: {}, ID do provedor: {}, Convite: {} (ID: {}), Data/Hora: {}",
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getApartment(),
            savedUser.getAuthProviderId(),
            invitation.getToken(),
            invitation.getId(),
            java.time.Instant.now()
        );

        return convertToUserDTO(savedUser);
    }

}