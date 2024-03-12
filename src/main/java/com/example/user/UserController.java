package com.example.user;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

import java.util.List;

@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/tout")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.FOUND);
    }

    // @PostMapping("/add")
    // @Transactional
    // public ResponseEntity<User> add(@RequestBody User user) {
    //     return ResponseEntity.ok(userService.add(user));
    // }

    @PostMapping("/ajouter/utilisateur")
    @Transactional
    public ResponseEntity<User> addUser(@RequestBody User user) {

        user.setRole(Role.UTILISATEUR);
        return ResponseEntity.ok(userService.add(user));
    }

    @PostMapping("/ajouter/admin")
    @Transactional
    public ResponseEntity<User> add(@RequestBody User user) {

        user.setRole(Role.ADMIN);
        return ResponseEntity.ok(userService.add(user));
    }

    @GetMapping("/{email}")
    public User getByEmail(@PathVariable("email") String email) {
        return userService.findByEmail(email);
    }

    @DeleteMapping("/{email}")
    @Transactional
    public void delete(@PathVariable("email") String email) {
        userService.deleteByEmail(email);
    }

    @PutMapping("/mettre_à_jour")
    @Transactional
    public ResponseEntity<User> update(@RequestBody User user) {

        return ResponseEntity.ok(userService.update(user));
    }


}
