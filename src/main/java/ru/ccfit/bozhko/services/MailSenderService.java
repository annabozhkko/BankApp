package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {
    private JavaMailSender mailSender;

    @Autowired
    public MailSenderService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String email, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password reset");
        message.setText("Your new password is: " + newPassword);
        mailSender.send(message);
    }
}
