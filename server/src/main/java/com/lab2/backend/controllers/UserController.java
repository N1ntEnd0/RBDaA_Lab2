package com.lab2.backend.controllers;

import com.lab2.backend.entity.User;
import com.lab2.backend.repository.UserRepository;
import com.lab2.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registration(@RequestBody User user) {
        if (this.userService.isValidUser(user)) {
            return new ResponseEntity<>("This user already exist", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>("Saved", HttpStatus.CREATED);
    }

    @PostMapping("/sign_in")
    public ResponseEntity<String> signIn(@RequestBody User user) {
        if (this.userService.isValidUser(user)) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
    }

}
