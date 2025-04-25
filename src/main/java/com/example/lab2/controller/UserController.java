package com.example.lab2.controller; // ✅ Добавляем строку с пакетом

import com.example.lab2.entities.SignupRequest;
import com.example.lab2.repository.UserRepository;
import com.example.lab2.lib.JwtCore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.lab2.services.UserService;
import com.example.lab2.entities.User;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;

    @Autowired
    private UserService userService;
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtCore jwtCore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.createUser(user.getUsername(), user.getPassword(), user.getEmail(), "USER");
        return ResponseEntity.ok("Пользователь создан!");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@RequestBody User user) {
        userService.createUser(user.getUsername(), user.getPassword(), user.getEmail(), "ADMIN");
        return ResponseEntity.ok("Администратор создан!");
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String jwt = token.substring(7);
        String username = jwtCore.getNameFromJwt(jwt);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String jwt = token.substring(7);
        String username = jwtCore.getNameFromJwt(jwt);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        User requestingUser = userRepository.findByUsername(username).orElse(null);

        if (requestingUser == null || !requestingUser.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        User userToDelete = userRepository.findById(id).orElse(null);

        if (userToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userRepository.delete(userToDelete);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/allusers")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
        }
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                        @RequestBody SignupRequest updatedUser) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String jwt = token.substring(7);
        String username = jwtCore.getNameFromJwt(jwt);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            user.setEmail(updatedUser.getEmail());
        }

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestHeader("ADMIN") String token,
                                        @RequestBody SignupRequest updatedUser) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String jwt = token.substring(7);
        String username = jwtCore.getNameFromJwt(jwt);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        User requestingUser = userRepository.findByUsername(username).orElse(null);

        if (requestingUser == null || !requestingUser.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        User userToUpdate = userRepository.findById(id).orElse(null);

        if (userToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            userToUpdate.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            userToUpdate.setEmail(updatedUser.getEmail());
        }

        userRepository.save(userToUpdate);
        return ResponseEntity.ok("User updated successfully");
    }

}
