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
@Table(name = "todoItems")
public class TodoItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)",name = "id")
    private UUID id;
    @Column(name = "listID")
    private UUID listID;
    @Column(name = "title", length = 255)
    private String title;
    @Column(name = "description", length = 255)
    private String description;
    @Column(name = "created", length = 255)
    private LocalDateTime created;
    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "dueDate", length = 255)
    private LocalDateTime dueDate;

    public TodoItems() {
    }

    public TodoItems(UUID id, UUID listID, String title, String description, LocalDateTime created, String status, LocalDateTime dueDate) {
        this.id = id;
        this.listID = listID;
        this.title = title;
        this.description = description;
        this.created = created;
        this.status = status;
        this.dueDate = dueDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getListID() {
        return listID;
    }

    public void setListID(UUID listID) {
        this.listID = listID;
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
