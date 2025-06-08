package com.todoList.todoList.API;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.todoList.todoList.Model.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AuthController {

    // ✅ Create a New Session
    @PostMapping("/addSession")
    public ResponseEntity<Map<String,String>> addSession(@RequestBody User requestData, 
                                                          HttpServletRequest request, 
                                                          HttpServletResponse response) {
        String username = requestData.getUsername(); // ✅ Get username from JSON request

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("username", username);
        session.setAttribute("checkLogin", "true");
        session.setMaxInactiveInterval(60*300); // 300 min session TTL

        // ✅ Secure Cookie for Session ID
        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
        sessionCookie.setMaxAge(60*300);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setAttribute("SameSite", "Strict"); // Prevent CSRF attacks

        response.addCookie(sessionCookie);

        if (session.getId() == null) {
            System.out.println("Session ID is null");
            return ResponseEntity.badRequest().body(null);
        }

        // System.out.println("Session created for user: " + username);


        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Session created");
        responseBody.put("sessionID", session.getId());
        responseBody.put("username", username);

        return ResponseEntity.ok().body(responseBody);
    }

    // ✅ Get Session Information
    @GetMapping("/getSession")
    public ResponseEntity<Map<String, Object>> getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: do not create new session if none exists

        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active session"));
        }

        // ✅ Convert session data into JSON response
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("message", "Session found");
        sessionData.put("username", session.getAttribute("username"));
        sessionData.put("sessionID", session.getId());
        sessionData.put("maxInactiveInterval", session.getMaxInactiveInterval());
        sessionData.put("creationTime", session.getCreationTime());
        sessionData.put("lastAccessedTime", session.getLastAccessedTime());
        return ResponseEntity.ok(sessionData);
    }

    @DeleteMapping("/deleteSession")
    public ResponseEntity<Map<String, String>> deleteSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active session"));
        }

        session.invalidate();
        Cookie sessionCookie = request.getCookies().clone()[0];
        sessionCookie.setMaxAge(0);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);
        
        return ResponseEntity.ok(Map.of("message", "Session deleted"));
    }
}
