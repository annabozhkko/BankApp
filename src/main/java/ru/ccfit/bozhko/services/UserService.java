package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.Role;
import ru.ccfit.bozhko.models.User;
import ru.ccfit.bozhko.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
@Transactional
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, MailSenderService mailSenderService){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    public boolean createUser(User user){
        if(repository.findByEmail(user.getEmail()) != null) {
            return false;
        }

        user.getRoles().add(Role.BANKER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        repository.save(user);
        return true;
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public void save(User user) {
        repository.save(user);
    }

    public void restorePassword(User user){
        String newPassword = generateNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
        mailSenderService.sendPasswordResetEmail(user.getEmail(), newPassword);
    }

    private String generateNewPassword() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] password = new byte[8];
        secureRandom.nextBytes(password);
        return Arrays.toString(password);
    }

}
