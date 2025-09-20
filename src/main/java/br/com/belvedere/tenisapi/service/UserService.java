package br.com.belvedere.tenisapi.service;

import br.com.belvedere.tenisapi.dto.UserDTO;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.enums.UserRole;
import br.com.belvedere.tenisapi.repository.UserRepository;
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
}