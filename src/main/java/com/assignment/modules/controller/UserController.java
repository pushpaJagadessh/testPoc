package com.assignment.modules.controller;

import com.assignment.modules.dto.UserDTO;
import com.assignment.modules.model.User;
import com.assignment.modules.service.AuditService;
import com.assignment.modules.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditService auditService;


//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
//    }
//@PostMapping
//    public ResponseEntity<?> createUser(@RequestBody User user) {
//        try {
//             ResponseEntity.ok(userService.createUser(user));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(users);
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {


        User createdUser = userService.createUser(user);
        if(createdUser != null) {
            auditService.logAudit(
                    user.getEmail(),            // username
                    "CREATE_USER",              // action
                    createdUser.getId(),        // entityId
                    "User created successfully",// details
                    request,                    // HttpServletRequest
                    201,                        // status code
                    "No error"                  // error message
            );
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }
        else {
            auditService.logAudit(
                    user.getEmail(),
                    "CREATE_USER_FAILED",
                    null,
                    "FAILED, CHECK ERROR",
                    request,
                    400,
                    "User with email " + user.getEmail() + " already exists."
            );
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + user.getEmail() + " already exists.");
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email, HttpServletRequest request) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            auditService.logAudit(
                    email,
                    "GET_USER_FAILED",
                    null,
                    "User not found",
                    request,
                    404,
                    "No user with given email"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with email " + email + " not found.");
        }

        auditService.logAudit(
                email,
                "GET_USER",
                user.getId(),
                "User fetched successfully",
                request,
                200,
                "No error"
        );
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/email/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody User user, HttpServletRequest request) {
        try {
            User updated = userService.updateUser(email, user);
            if (updated != null) {
                auditService.logAudit(
                        email,
                        "UPDATE_USER",
                        updated.getId(),
                        "User updated successfully",
                        request,
                        200,
                        "No error"
                );
                return ResponseEntity.status(HttpStatus.OK).body(updated);
            } else {
                auditService.logAudit(
                        email,
                        "UPDATE_USER_FAILED",
                        null,
                        "User not found",
                        request,
                        404,
                        "No user with given email"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with email " + email + " not found.");
            }
        } catch (Exception e) {
            auditService.logAudit(
                    email,
                    "UPDATE_USER_ERROR",
                    null,
                    "Unexpected error: " + e.getMessage(),
                    request,
                    500,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred.");
        }
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email, HttpServletRequest request) {
        User user = userService.deleteUser(email);
        if (user == null) {
            auditService.logAudit(
                    email,
                    "DELETE_USER_FAILED",
                    null,
                    "User not found",
                    request,
                    404,
                    "No user with given email"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with email " + email + " not found.");
        }

        auditService.logAudit(
                email,
                "DELETE_USER",
                user.getId(),
                "User deleted successfully",
                request,
                202,
                "No error"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("User with email " + email + " deleted");
    }

}
