package com.SpringBoot.demo.controllers;

import com.SpringBoot.demo.Service.EmailService;
import com.SpringBoot.demo.Service.NotificationService;
import com.SpringBoot.demo.models.Task;
import com.SpringBoot.demo.models.User;
import com.SpringBoot.demo.repositories.TaskRepository;
import com.SpringBoot.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/test-email")
    public String testEmail() {
        emailService.sendApprovalEmail("lotfitoumi56@gmail.com");
        return "Email sent";
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a single user by ID
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    // Approve a user
    @PutMapping("/users/approve/{id}")
    public User approveUser(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUserActive(true);
        User savedUser = userRepository.save(user);

        // send approval email
        try {
            emailService.sendApprovalEmail(savedUser.getEmail());
        } catch (Exception e) {
            e.printStackTrace(); // check console for errors
        }

        return savedUser;
    }

    // Update a user
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setSecretPassword(updatedUser.getSecretPassword());
            user.setUserActive(updatedUser.getUserActive());
            return userRepository.save(user);
        }
        return null;
    }

    // Create user
    @PostMapping("/users/new")
    public User createUser(@RequestBody User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUserActive(true); // mark as active immediately
        return userRepository.save(user);

    }


    //  Delete a user
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User deleted successfully!";
        }
        return "User not found!";
    }
    // --- TASK CRUD ---

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task task) {
        task.setCreatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);

        // Send notification to assigned user
        notificationService.createNotification(
                task.getAssignedToUserId(),
                "New Task Assigned",
                "You have been assigned a new task: " + task.getTitle()
        );

        return savedTask;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/tasks/{id}")
    public Task getTaskById(@PathVariable Integer id) {
        return taskRepository.findById(id).orElse(null);
    }

    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setAssignedToUserId(taskDetails.getAssignedToUserId());

        return taskRepository.save(task);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Integer id) {
        taskRepository.deleteById(id);
    }

    // --- ADMIN SEND CUSTOM NOTIFICATION ---

    @PostMapping("/notifications")
    public void sendNotification(@RequestParam Integer userId,
                                 @RequestParam String title,
                                 @RequestParam String message) {
        notificationService.createNotification(userId, title, message);
    }

    // --- Anotify --- user
    @PostMapping("/notify/{userId}")
    public String notifyUser(
            @PathVariable Integer userId,
            @RequestParam String message
    ) {
        notificationService.createNotification(userId, "Admin Message", message);
        return "Notification sent to user " + userId;
    }

}
