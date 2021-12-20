package com.lab2.backend.services;

import com.lab2.backend.entity.User;
import com.lab2.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean isValidUser (User user) {
        try {
            User targetUser = this.userRepository.findByLogin(user.getLogin());
            return targetUser != null && Objects.equals(targetUser.getPassword(), user.getPassword());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
