package br.com.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.email.dto.EmailVerificationConsumer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Serviço para envio de e-mails no sistema bancário
 * Responsável pelo envio de e-mails de verificação e notificações aos usuários
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 * @author Pablo R.
 */
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * Construtor para injeção de dependência do JavaMailSender
     * @param mailSender Sender configurado para envio de e-mails
     */
    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.javaMailSender = mailSender;
    }

    /**
     * Envia e-mail de verificação com código de confirmação
     *
     * @param consumer DTO contendo e-mail do destinatário e código de verificação
     * @throws MessagingException Em caso de erro durante o envio do e-mail
     * @implNote Utiliza MimeMessage para suportar conteúdo HTML
     * @security O código de verificação tem validade de 10 minutos
     *
     * @example
     * EmailVerificationConsumer data = new EmailVerificationConsumer(
     *     "usuario@email.com",
     *     "123456"
     * );
     * emailService.sendEmail(data);
     */
    public void sendEmail(EmailVerificationConsumer consumer) throws MessagingException {
        String subject = "Confirm your e-mail - verification code"; //Subject = Título da mensagem
        //Body = Conteúdo da mensagem
        String body = """
                <h1>Hello!</h1>
                <p>Use this code for confirm your email</p>
                <h2 style="color:blue">%s</h2>
                <p>This code expire in 10 minutes</p>
                """.formatted(consumer.code());

        //Criamos um envelope vazio
        MimeMessage message = this.javaMailSender.createMimeMessage();
        //E aqui colocamos os conteúdos dentro do envelope
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(consumer.email());
        helper.setSubject(subject);
        helper.setText(body, true);

        //E por fim enviamos o e-mail ao usuário.
        this.javaMailSender.send(message);
    }
}