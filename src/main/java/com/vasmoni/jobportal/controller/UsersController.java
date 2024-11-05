package com.vasmoni.jobportal.controller;

import com.vasmoni.jobportal.entity.Users;
import com.vasmoni.jobportal.entity.UsersType;
import com.vasmoni.jobportal.service.ISecurityUserService;
import com.vasmoni.jobportal.service.UsersService;
import com.vasmoni.jobportal.service.UsersTypeService;
import com.vasmoni.jobportal.util.GenericResponse;
import com.vasmoni.jobportal.util.PasswordDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


@Controller
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    private JavaMailSender mailSender;
    @Autowired
    private ISecurityUserService securityUserService;

    private final MessageSource messages;
    private final Environment env;


    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService, MessageSource messages, Environment env) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
        this.messages = messages;
        this.env = env;
    }

    @GetMapping("/register")
    public String register(Model model) {
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users, Model model) {
        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
        if (optionalUsers.isPresent()) {
            model.addAttribute("error", "Email already registered,try to login or register with other email.");
            List<UsersType> usersTypes = usersTypeService.getAll();
            model.addAttribute("getAllTypes", usersTypes);
            model.addAttribute("user", new Users());
            return "register";
        }
        usersService.addNew(users);
        return "redirect:/dashboard/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/";
    }

    @GetMapping("/reset-password")
    public String forgetPassword() {
        return "forgetPassword";
    }

    @PostMapping("/user/resetPassword")
    public GenericResponse resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {
        Optional<Users> user = usersService.getUserByEmail(userEmail);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + userEmail);
        }
        String token = UUID.randomUUID().toString();
        usersService.createPasswordResetTokenForUser(user.get(), token);
        mailSender.send(constructResetTokenEmail(getAppUrl(request),
                request.getLocale(), token, user.get()));
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null,
                        request.getLocale()));
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        String result = securityUserService.validatePasswordResetToken(token);
        if (result != null) {
            String message = messages.getMessage("auth.message." + result, null, locale);
            return "redirect:/login.html?lang="
                    + locale.getLanguage() + "&message=" + message;
        } else {
            model.addAttribute("token", token);
            return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
        }
    }

    @PostMapping("/user/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

        String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            return new GenericResponse(messages.getMessage(
                    "auth.message." + result, null, locale));
        }

        Optional<Users> user = usersService.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            usersService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage(
                    "message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage(
                    "auth.message.invalid", null, locale));
        }
    }

    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, Users user) {
        String url = contextPath + "/user/changePassword?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             Users user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}