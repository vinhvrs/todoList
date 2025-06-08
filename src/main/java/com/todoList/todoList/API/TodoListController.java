package com.todoList.todoList.API;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todoList.Model.TodoItems;
import com.todoList.todoList.Model.TodoList;
import com.todoList.todoList.repo.ItemsRepo;
import com.todoList.todoList.repo.TodoRepo;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TodoListController {
    @Autowired
    private TodoRepo todoRepo;

    @Autowired
    private ItemsRepo itemsRepo;

    @GetMapping("/api/{id}/getList")
    public ResponseEntity<List<TodoList>> getList(@PathVariable UUID id) {
        List<TodoList> todos = todoRepo.findByUserUUID(id);
        
        if (todos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no todos found
        }

        return ResponseEntity.ok(todos);
    }
    
    @PostMapping("/api/{id}/addList")
    public ResponseEntity<TodoList> createList(@PathVariable UUID id, @RequestBody TodoList entity) {
        TodoList newList = new TodoList();
        TodoList checkList = todoRepo.findByListTitle(entity.getTitle());
        if (checkList != null) {
            return ResponseEntity.badRequest().body(null);
        }
        newList.setTitle(entity.getTitle());
        newList.setuserID(id);
        newList.setDescription(entity.getDescription());
        newList.setDate(LocalDateTime.now());
        newList.setStatus("Incomplete");
        todoRepo.save(newList);
        return ResponseEntity.ok(todoRepo.findByListTitle(newList.getTitle()));
    }

    @DeleteMapping("/api/{id}/deleteList/{listID}")
    public String deleteList(@PathVariable UUID id, @PathVariable UUID listID){
        String nameList = todoRepo.findByListUUID(id, listID).getTitle();
        todoRepo.deleteById(listID);
        return "Deleted list with name: " + nameList;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @GetMapping("/api/{id}/getListItems/{listID}")
    public ResponseEntity<List<TodoItems>> getListItems(@PathVariable UUID id, @PathVariable UUID listID) {
        List<TodoItems> items = itemsRepo.findByListID(listID);
        if (items.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(items);
    }

    @PostMapping("/api/{id}/addListItem/{listID}")
    public ResponseEntity<TodoItems> createListItem(@PathVariable UUID id, @PathVariable UUID listID, @RequestBody TodoItems entity) {
        TodoItems newItem = new TodoItems();
        newItem.setListID(listID);
        newItem.setTitle(entity.getTitle());
        newItem.setDescription(entity.getDescription());
        newItem.setCreated(LocalDateTime.now());
        newItem.setStatus("Incomplete");
        newItem.setDueDate(entity.getDueDate());
        itemsRepo.save(newItem);
        return ResponseEntity.ok(newItem);
    }

    // @PostMapping("/api/{id}/addListItem/null")
    // public ResponseEntity<TodoItems> createListItemNull(@RequestBody TodoItems entity) {
    //     TodoItems newItem = new TodoItems();
    //     UUID listID = UUID.randomUUID(); // Generate a new UUID for the listID
    //     newItem.setListID(listID);
    //     newItem.setTitle(entity.getTitle());
    //     newItem.setDescription(entity.getDescription());
    //     newItem.setCreated(LocalDateTime.now());
    //     newItem.setStatus("Incomplete");
    //     newItem.setDueDate(entity.getDueDate());
    //     itemsRepo.save(newItem);
    //     return ResponseEntity.ok(newItem);
    // }

    @PutMapping("/api/{id}/updateListItem/{listID}/{itemID}")
    public String updateListItem(@PathVariable UUID id, @PathVariable UUID listID, @PathVariable UUID itemID, @RequestBody TodoItems entity) {
        TodoItems item = itemsRepo.findByID(itemID).get(0);
        item.setTitle(entity.getTitle());
        item.setDescription(entity.getDescription());
        item.setDueDate(entity.getDueDate());
        item.setStatus(entity.getStatus());
        itemsRepo.save(item);
        
        return "Update successful!";
    }

    @PutMapping("/api/{id}/updateListItemStatus/{listID}/{itemID}")
    public String updateListItemStatus(@PathVariable UUID id, @PathVariable UUID listID, @PathVariable UUID itemID, @RequestBody TodoItems entity) {
        TodoItems item = itemsRepo.findByID(itemID).get(0);
        item.setStatus(entity.getStatus());
        itemsRepo.save(item);
        
        return "Update successful!";
    }

    @DeleteMapping("/api/{id}/deleteListItem/{listID}/{itemID}")
    public String deleteListItem(@PathVariable UUID id, @PathVariable UUID listID, @PathVariable UUID itemID){
        String nameItem = itemsRepo.findByID(itemID).get(0).getTitle();
        itemsRepo.deleteById(itemID);
        return "Deleted item with name: " + nameItem;
    }


    @GetMapping("/api/{userID}/getCalendarItems/{date}")
    public ResponseEntity<List<TodoItems>> getTasksByDate(@PathVariable String userID, @PathVariable String date) {
        List<TodoItems> items = itemsRepo.findByDueDate(date, userID);
        // if (items.isEmpty()) {
        //     TodoItems emptyItem = new TodoItems();
        //     emptyItem.setTitle("No tasks found.");
        //     items.add(emptyItem);
        //     return ResponseEntity.status(200).body(items);
        // }
        return ResponseEntity.ok(items);
    }
}
