package com.todoList.todoList.API;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class PageController {
    @GetMapping({"/", "/home", "/index"})
    public String index() {
        return "index"; // Return the name of the HTML file (index.html) in the templates directory
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Return the name of the HTML file (login.html) in the templates directory
    }

    @GetMapping("/register")
    public String register() {
        return "login"; // Return the name of the HTML file (register.html) in the templates directory
    }

    @GetMapping("/calendar_view")
    public String calendarView() {
        return "calendarView"; // Return the name of the HTML file (calendarView.html) in the templates directory
    }

    @GetMapping("/todo_list/create")
    public String createTodo() {
        return "createList"; // Return the name of the HTML file (createList.html) in the templates directory
    }

    @GetMapping("/user/info")
    public String getUserInfo(@RequestParam String param) {
        return "updateInfo"; // Return the name of the HTML file (updateInfo.html) in the templates directory
    }
    
    @GetMapping("/user/changePassword")
    public String changePassword() {
        return "changePassword"; // Return the name of the HTML file (changePassword.html) in the templates directory
    }

}
