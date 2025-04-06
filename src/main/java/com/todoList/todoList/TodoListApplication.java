package com.todoList.todoList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TodoListApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(TodoListApplication.class, args);
	}

}
