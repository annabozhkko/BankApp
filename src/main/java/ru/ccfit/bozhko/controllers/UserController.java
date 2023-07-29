package ru.ccfit.bozhko.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ccfit.bozhko.models.Role;
import ru.ccfit.bozhko.models.User;
import ru.ccfit.bozhko.services.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("roles", Role.values());
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if(!userService.createUser(user)){
            model.addAttribute("errorMessage", "User already exists");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String restorePassword(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);

        if (user == null) {
            model.addAttribute("errorMessage", "User not found");
            return "forgotPassword";
        }

        userService.restorePassword(user);
        return "redirect:/login";
    }
}
