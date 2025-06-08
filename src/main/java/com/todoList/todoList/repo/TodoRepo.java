package com.todoList.todoList.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.todoList.todoList.Model.TodoList;

public interface TodoRepo extends JpaRepository<TodoList, UUID>{
    
    @Query("SELECT t FROM TodoList t WHERE t.userID = ?1")
    public List<TodoList> findByUserUUID(UUID userID);

    @Query("SELECT t FROM TodoList t WHERE t.userID = ?1 and t.listID = ?2")
    public TodoList findByListUUID(UUID userID, UUID listID);

    @Query("SELECT t FROM TodoList t WHERE t.title = ?1")
    public TodoList findByListTitle(String title);

}
