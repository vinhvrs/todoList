package com.todoList.todoList.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemsRepo extends JpaRepository<TodoItems, UUID> {    
    @Query("SELECT i FROM TodoItems i WHERE i.listID = ?1")
    public List<TodoItems> findByListID(UUID listID);
    
    @Query("SELECT i FROM TodoItems i WHERE i.id = ?1")
    public List<TodoItems> findByID(UUID id);
    
    // @Query(value = "SELECT * FROM todo_items WHERE due_date LIKE CONCAT(?1, '%')", nativeQuery = true)
    // List<TodoItems> findByDueDate(String dueDate);

    @Query(value = "SELECT * FROM todo_items WHERE due_date LIKE CONCAT(?1, '%') AND listid IN (SELECT listid FROM todo_table WHERE userid = UNHEX(REPLACE(?2, '-', '')))", nativeQuery = true)
    public List<TodoItems> findByDueDate(String dueDate, String userId);

}
