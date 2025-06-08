package com.todoList.todoList.Model;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Ensure table name matches exactly
    public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  // Now ID can auto-increment
    @Column(columnDefinition = "BINARY(16)",name = "id")
    private UUID userID;  

    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 255)
    private String phone;

    // Default constructor (Required by JPA)
    public User() {}

    // Constructor with all fields
    public User(UUID userID, String username, String password, String email, String phone) {
        this.userID = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    private String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    // Getters and Setters
    public UUID getId() { return this.userID; }
    public void setId(UUID userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
