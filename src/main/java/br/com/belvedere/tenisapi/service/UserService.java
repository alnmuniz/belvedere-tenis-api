package br.com.belvedere.tenisapi.service;

import br.com.belvedere.tenisapi.dto.UserDTO;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.entity.Booking;
import br.com.belvedere.tenisapi.enums.UserRole;
import br.com.belvedere.tenisapi.enums.UserStatus;
import br.com.belvedere.tenisapi.repository.UserRepository;
import br.com.belvedere.tenisapi.repository.BookingRepository;
import br.com.belvedere.tenisapi.dto.UserRegistrationRequestDTO;
import br.com.belvedere.tenisapi.entity.Invitation;
import br.com.belvedere.tenisapi.enums.InvitationStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

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

    @Transactional
    public UserDTO demoteUserToUser(Long userId, String adminAuthProviderId) {
        // Valida se o admin existe
        User adminUser = userRepository.findByAuthProviderId(adminAuthProviderId)
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));

        // 1. Encontra o usuário no banco
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + userId + " não encontrado."));

        // 2. Altera o papel (role) para USER
        user.setRole(UserRole.USER);

        // 3. Salva a alteração
        User updatedUser = userRepository.save(user);

        // 4. Registra o log da operação administrativa
        logger.info("Usuário com perfil alterado para usuário comum - " +
        "Usuário alterado: {} (ID: {}, Apartamento: {}), " +
        "Admin responsável: {} (ID: {})",
        user.getName(),
        user.getId(),
        user.getApartment(),
        adminUser.getName(),
        adminUser.getId());

        // 5. Converte para DTO e retorna 
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
        dto.setStatus(user.getStatus());
        dto.setAuthProviderId(user.getAuthProviderId());
        return dto;
    }

    // Método privado para remover reservas futuras de um usuário
    private int removeFutureBookingsForUser(Long userId, User adminUser) {
        List<Booking> futureBookings = bookingRepository.findFutureBookingsByUserId(userId, Instant.now());
        int removedBookingsCount = futureBookings.size();
        
        if (!futureBookings.isEmpty()) {
            bookingRepository.deleteAll(futureBookings);
            logger.info("Reservas futuras removidas automaticamente - " +
                    "Usuário: {} (ID: {}), " +
                    "Quantidade de reservas removidas: {}, " +
                    "Admin responsável: {} (ID: {})",
                    adminUser.getName(),
                    userId,
                    removedBookingsCount,
                    adminUser.getName(),
                    adminUser.getId());
        }
        
        return removedBookingsCount;
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

    @Transactional
    public UserDTO blockUser(Long userId, String adminAuthProviderId) {
        // Valida se o admin existe
        User adminUser = userRepository.findByAuthProviderId(adminAuthProviderId)
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));

        // 1. Encontra o usuário no banco
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + userId + " não encontrado."));

        // 2. Remove todas as reservas futuras do usuário
        int removedBookingsCount = removeFutureBookingsForUser(userId, adminUser);

        // 3. Altera o status para INACTIVE
        user.setStatus(UserStatus.INACTIVE);

        // 4. Salva a alteração
        User updatedUser = userRepository.save(user);

        // 5. Registra o log da operação administrativa
        logger.info("Usuário bloqueado - " +
        "Usuário bloqueado: {} (ID: {}, Apartamento: {}), " +
        "Reservas futuras removidas: {}, " +
        "Admin responsável: {} (ID: {})",
        user.getName(),
        user.getId(),
        user.getApartment(),
        removedBookingsCount,
        adminUser.getName(),
        adminUser.getId());

        // 6. Converte para DTO e retorna
        return convertToUserDTO(updatedUser);
    }

    @Transactional
    public UserDTO unblockUser(Long userId, String adminAuthProviderId) {
        // Valida se o admin existe
        User adminUser = userRepository.findByAuthProviderId(adminAuthProviderId)
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));

        // 1. Encontra o usuário no banco
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + userId + " não encontrado."));

        // 2. Altera o status para ACTIVE
        user.setStatus(UserStatus.ACTIVE);

        // 3. Salva a alteração
        User updatedUser = userRepository.save(user);

        // 4. Registra o log da operação administrativa
        logger.info("Usuário desbloqueado - " +
        "Usuário desbloqueado: {} (ID: {}, Apartamento: {}), " +
        "Admin responsável: {} (ID: {})",
        user.getName(),
        user.getId(),
        user.getApartment(),
        adminUser.getName(),
        adminUser.getId());

        // 5. Converte para DTO e retorna
        return convertToUserDTO(updatedUser);
    }

    /**
     * Método para buscar todos os usuários
     * @return Lista de usuários
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAllOrdered().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

}