package br.com.belvedere.tenisapi.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import br.com.belvedere.tenisapi.entity.Invitation;
import br.com.belvedere.tenisapi.entity.User;
import br.com.belvedere.tenisapi.enums.InvitationStatus;
import br.com.belvedere.tenisapi.enums.UserRole;
import br.com.belvedere.tenisapi.repository.InvitationRepository;
import br.com.belvedere.tenisapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.core.io.ClassPathResource;

@Service
public class InvitationService {

    private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public void createAndSendInvitation(String email, String apartment, String adminAuthProviderId) {
        // 1. Valida se o admin existe e tem permissão
        User adminUser = userRepository.findByAuthProviderId(adminAuthProviderId)
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));

        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem enviar convites.");
        }

        // 2. Gera um token único e seguro
        String token = UUID.randomUUID().toString();

        // 3. Cria e salva a entidade de convite no banco
        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setApartment(apartment);
        invitation.setToken(token);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS)); // Convite válido por 7 dias
        Invitation savedInvitation = invitationRepository.save(invitation);

        // 4. Envia o e-mail
        sendInvitationEmail(email, token);

        // 5. Registra o log de auditoria
        logger.info("Convite enviado - ID do convite: {}, " +
                "Email convidado: {}, " +
                "Apartamento: {}, " +
                "Status: {}, " +
                "Token: {}, " +
                "Expira em: {}, " +
                "Admin responsável: {} (ID: {}, Apartamento: {})",
                savedInvitation.getId(),
                email,
                apartment,
                InvitationStatus.PENDING.getValue(),
                token,
                savedInvitation.getExpiresAt(),
                adminUser.getName(),
                adminUser.getId(),
                adminUser.getApartment());
    }

    private void sendInvitationEmail(String toEmail, String token) {
        Email from = new Email(fromEmail);
        String subject = "🏆 Convite para o Sistema de Reservas - Condomínio Belvedere";
        Email to = new Email(toEmail);

        // Constrói a URL completa do frontend com o token
        String registrationUrl = frontendUrl + "/register?token=" + token;
        
        // Conteúdo HTML profissional do email
        String htmlContent = buildProfessionalEmailHTML(registrationUrl);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new RuntimeException("Falha ao enviar e-mail de convite. Status: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao enviar e-mail de convite.", ex);
        }
    }

    private String buildProfessionalEmailHTML(String registrationUrl) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/invitation-email.html");
            InputStream inputStream = resource.getInputStream();
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            
            // Substitui o placeholder pela URL de registro
            return htmlTemplate.replace("{REGISTRATION_URL}", registrationUrl);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar template de e-mail", e);
        }
    }
}