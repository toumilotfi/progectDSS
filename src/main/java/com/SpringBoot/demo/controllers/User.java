package com.SpringBoot.demo.controllers;

import com.SpringBoot.demo.Service.AuthService;
import com.SpringBoot.demo.Service.NotificationService;
import com.SpringBoot.demo.models.Notification;
import com.SpringBoot.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class User {

    @Autowired
    private AuthService authService;

    private UserRepository userRepository;
    private NotificationService notificationService;

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        boolean valid = authService.authenticate(email, password);
        System.out.println("alpha2");
        return valid ? "Authentication successful!" : "Invalid email or password.";
    }

    @GetMapping("/test")
    public String test() {
        return "AUTH OK";
    }


    // REGISTER
    @PostMapping("/register")
    public com.SpringBoot.demo.models.User register(@RequestBody com.SpringBoot.demo.models.User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUserActive(false);
        return userRepository.save(user);
    }


    // View notifications
    @GetMapping("/not/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Integer userId) {
        return notificationService.getUserNotifications(userId);
    }

    // Mark as read
    @PutMapping("/read/{id}")
    public String markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }

}