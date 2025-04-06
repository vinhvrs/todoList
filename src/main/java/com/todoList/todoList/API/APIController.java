package com.todoList.todoList.API;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.todoList.todoList.Model.User;
import com.todoList.todoList.Model.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.todoList.todoList.Model.ItemsRepo;
import com.todoList.todoList.Model.TodoItems;
import com.todoList.todoList.Model.TodoList;
import com.todoList.todoList.Model.TodoRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
public class APIController {

    @Autowired
    private UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/api/user/getUser")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public List<User> getUser(){
        return userRepo.findAll();
    }

    @PostMapping("/api/user/register")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
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
    @CrossOrigin(origins = "http://127.0.0.1:5500/", allowCredentials = "true")
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
    @CrossOrigin(origins = "http://127.0.0.1:5500/", allowCredentials = "true")
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        AuthController authController = new AuthController();
        authController.deleteSession(request, response);
        if (request.getSession(false) == null) {
            return ResponseEntity.badRequest().body("Error logging out");
        }
        return ResponseEntity.ok("User logged out successfully!");
    }

    @PutMapping("/api/user/{id}/updateUser")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public String updateUser(@PathVariable UUID id, @RequestBody User entity) {
        User user = userRepo.findByUUID(id);
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPhone(entity.getPhone());
        userRepo.save(user);
        
        return "Update successful!";
    }

    @PutMapping("/api/user/{id}/changePassword")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public String changePassword(@PathVariable UUID id, @RequestBody User entity) {
        User user = userRepo.findByUUID(id);
        user.setPassword(passwordEncoder.encode(entity.getPassword()));
        userRepo.save(user);

        return "Password changed successfully!";
    }
    
    @DeleteMapping("/api/deleteUser/{id}")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public String deleteUser(@PathVariable UUID id){
        userRepo.deleteById(id);
        return "Deleted user with ID: " + id;
    }
    
    @Autowired
    private TodoRepo todoRepo;

    @GetMapping("/api/{id}/getList")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public ResponseEntity<List<TodoList>> getList(@PathVariable UUID id) {
        List<TodoList> todos = todoRepo.findByUserUUID(id);
        
        if (todos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no todos found
        }

        return ResponseEntity.ok(todos);
    }

    @PostMapping("/api/{id}/addList")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
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
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public String deleteList(@PathVariable UUID id, @PathVariable UUID listID){
        String nameList = todoRepo.findByListUUID(id, listID).getTitle();
        todoRepo.deleteById(listID);
        return "Deleted list with name: " + nameList;
    }


    @Autowired
    private ItemsRepo itemsRepo;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @GetMapping("/api/{id}/getListItems/{listID}")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public ResponseEntity<List<TodoItems>> getListItems(@PathVariable UUID id, @PathVariable UUID listID) {
        List<TodoItems> items = itemsRepo.findByListID(listID);
        if (items.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(items);
    }

    @PostMapping("/api/{id}/addListItem/{listID}")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
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

    @PutMapping("/api/{id}/updateListItem/{listID}/{itemID}")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
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
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public String updateListItemStatus(@PathVariable UUID id, @PathVariable UUID listID, @PathVariable UUID itemID, @RequestBody TodoItems entity) {
        TodoItems item = itemsRepo.findByID(itemID).get(0);
        item.setStatus(entity.getStatus());
        itemsRepo.save(item);
        
        return "Update successful!";
    }

    @DeleteMapping("/api/{id}/deleteListItem/{listID}/{itemID}")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public String deleteListItem(@PathVariable UUID id, @PathVariable UUID listID, @PathVariable UUID itemID){
        String nameItem = itemsRepo.findByID(itemID).get(0).getTitle();
        itemsRepo.deleteById(itemID);
        return "Deleted item with name: " + nameItem;
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/");
        }
    }

    @GetMapping("/api/{userID}/getCalendarItems/{date}")
    @CrossOrigin(origins = "http://127.0.0.1:5500/")
    public ResponseEntity<List<TodoItems>> getTasksByDate(@PathVariable String userID, @PathVariable String date) {
        List<TodoItems> items = itemsRepo.findByDueDate(date, userID);
        if (items.isEmpty()) {
            TodoItems emptyItem = new TodoItems();
            emptyItem.setTitle("No tasks found.");
            items.add(emptyItem);
            return ResponseEntity.badRequest().body(items);
        }
        return ResponseEntity.ok(items);
    }
    

}
