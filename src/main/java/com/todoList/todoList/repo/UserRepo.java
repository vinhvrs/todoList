package com.todoList.todoList.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.todoList.todoList.Model.User;

public interface UserRepo extends JpaRepository<User, UUID>{
    @Query("SELECT u FROM User u WHERE u.username = ?1")    
    public User findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public List<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.phone = ?1")
    public List<User> findByPhone(String phone);
    @Query("SELECT u FROM User u Where u.id = ?1")
    public User findByUUID(UUID id);
}
