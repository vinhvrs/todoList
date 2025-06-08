package com.todoList.todoList.API;

import org.springframework.web.bind.annotation.RestController;

import com.todoList.todoList.Model.User;
import com.todoList.todoList.repo.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class APIController {

    @Autowired
    private UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/api/user/getUser")
    public List<User> getUser(){
        return userRepo.findAll();
    }

    @PostMapping("/api/user/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        // Hash password before saving
        User newUser = user;
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<User> loginUser(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        User existingUser = userRepo.findByUsername(user.getUsername());
        AuthController authController = new AuthController();
        if (existingUser == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body(null);
        }
        authController.addSession(existingUser, request, response);
        return ResponseEntity.ok(existingUser);
    }

    @PostMapping("/api/user/logout")
    
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        AuthController authController = new AuthController();
        authController.deleteSession(request, response);
        if (request.getSession(false) == null) {
            return ResponseEntity.badRequest().body("Error logging out");
        }
        return ResponseEntity.ok("User logged out successfully!");
    }

    @PutMapping("/api/user/{id}/updateUser")
    public String updateUser(@PathVariable UUID id, @RequestBody User entity) {
        User user = userRepo.findByUUID(id);
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPhone(entity.getPhone());
        userRepo.save(user);
        
        return "Update successful!";
    }

    @PutMapping("/api/user/{id}/changePassword")
    public String changePassword(@PathVariable UUID id, @RequestBody User entity) {
        User user = userRepo.findByUUID(id);
        user.setPassword(passwordEncoder.encode(entity.getPassword()));
        userRepo.save(user);

        return "Password changed successfully!";
    }
    
    @DeleteMapping("/api/deleteUser/{id}")
    public String deleteUser(@PathVariable UUID id){
        userRepo.deleteById(id);
        return "Deleted user with ID: " + id;
    }
    
    
}
