package com.todoList.todoList.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "todoTable")
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)", name = "listID")
    private UUID listID;
    @Column(name = "userID")
    private UUID userID;
    @Column(name = "title", length = 255)
    private String title;
    @Column(name = "description", length = 255)
    private String description;
    @Column(name = "date", length = 255)
    private LocalDateTime date;
    @Column(name = "status", length = 255)
    private String status;

    public TodoList() {
    }

    public TodoList(UUID listID, UUID userID, String title, String description, LocalDateTime date, String status) {
        this.listID = listID;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    public UUID getlistID() {
        return listID;
    }

    public void setlistID(UUID listID) {
        this.listID = listID;
    }

    public UUID getuserID() {
        return userID;
    }

    public void setuserID(UUID userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
