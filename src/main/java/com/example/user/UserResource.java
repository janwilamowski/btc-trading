package com.example.trading.user;

import com.example.trading.transaction.Order;
import com.example.trading.transaction.Transaction;
import com.example.trading.transaction.TransactionService;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
public class UserResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService txService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return user.get();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable long id) {
        // Optional<User> user = userRepository.findById(id);
        // if (users.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        // }
        try {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User savedUser = userRepository.save(newUser);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(savedUser.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    /* TRANSACTIONS */

    @GetMapping("/users/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable long id) {
        User user = getUser(id);
        return user.getTransactions();
    }

    @PostMapping("/users/{id}/transactions")
    @Transactional
    public Transaction getTransactions(@PathVariable long id, @RequestBody Order order) {
        User user = getUser(id);
        try {
            return txService.createTransaction(user, order);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
