package com.lab2.backend.controllers;


import com.lab2.backend.entity.Task;
import com.lab2.backend.entity.User;
import com.lab2.backend.repository.TasksRepository;
import com.lab2.backend.repository.UserRepository;
import com.lab2.backend.services.TaskService;
import com.lab2.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TasksController {
    @Autowired
    private TasksRepository tasksRepository;


    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addTask(@RequestBody Task task){
        if (this.userService.isValidUser(task.getUser())) {
            tasksRepository.save(task);
            return new ResponseEntity<>("Saved", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Invalid user", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/last")
    public ResponseEntity<String> getLast(@RequestBody User user){
        if (this.userService.isValidUser(user)) {
            return new ResponseEntity(taskService.getLastTask(user.getLogin()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid user", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/all")
    public ResponseEntity<String> getAll(@RequestBody User user){
        if (this.userService.isValidUser(user)) {
            return new ResponseEntity(tasksRepository.findAllByUserLogin(user.getLogin()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid user", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Object> searchTask(@RequestParam(required = false) String tag, @RequestBody User user){
        if (this.userService.isValidUser(user)) {
            List<Task> tasks;
            if (tag != null){
                tasks = taskService.getTasksByTag(tag, user.getLogin());
                return new ResponseEntity(tasks, HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Invalid user", HttpStatus.UNAUTHORIZED);
        }
    }
}
