package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        log.info("UserController - findById() invoked with: {}", id);
//        return ResponseEntity.of(userRepository.findById(id));

        Optional<User> user = userRepository.findById(id);

        if (user == null) {
            log.error("UserController - findById() User not found");
            return ResponseEntity.notFound().build();
        } else {
            log.info("UserController - findById() OK, item id: {}", id);
            return ResponseEntity.ok(user.get());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        log.info("UserController - findByUserName() invoked with: {}", username);
        User user = userRepository.findByUsername(username);
//        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);

        if (user == null) {
            log.error("UserController - findByUserName() User not found: {}", username);
            return ResponseEntity.notFound().build();
        } else {
            log.info("UserController - findByUserName() OK, user name: {}", user.getUsername());
            return ResponseEntity.ok(user);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("UserController - createUser() invoked with: {}", createUserRequest.getUsername());
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        log.info("UserController - createUser() User name set with '" + createUserRequest.getUsername() + "'");

        if (createUserRequest.getPassword().length() < 10 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("UserController - createUser() Invalid password: {}", createUserRequest.getPassword());
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

        Cart cart = new Cart();
        cartRepository.save(cart);

        user.setCart(cart);
        userRepository.save(user);

        log.info("UserController - createUser() OK, user name: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

}
