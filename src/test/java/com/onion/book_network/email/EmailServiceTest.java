package com.onion.book_network.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail_shouldSendWithCorrectRecipientAndContent() throws Exception {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Use Context.class instead of IContext to avoid ambiguity
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html>Email Content</html>");

        // Act
        emailService.sendEmail(
                "to@mail.com",
                "Test User",
                EmailTemplateName.ACTIVATE_ACCOUNT,
                "http://localhost/activate",
                "123456",
                "Account activation"
        );

        // Assert
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq(EmailTemplateName.ACTIVATE_ACCOUNT.name()), any(Context.class));
    }
}
