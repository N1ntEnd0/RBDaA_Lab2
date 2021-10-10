package com.lab2.backend.controllers;


import com.lab2.backend.entity.Task;
import com.lab2.backend.repository.TasksRepository;
import com.lab2.backend.services.TaskService;
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

    @PostMapping("/add")
    public ResponseEntity<String> addTask(@RequestBody Task book){
        tasksRepository.save(book);
        return new ResponseEntity<>("Saved", HttpStatus.CREATED);
    }

    @GetMapping("/last")
    public ResponseEntity<String> getLast(){
        return new ResponseEntity(taskService.getLastTask(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAll(){
        return new ResponseEntity(tasksRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Task> searchTask(@RequestParam(required = false) String tag){
        List<Task> tasks;
        if (tag != null){
            tasks = taskService.getTasksByTag(tag);
            return new ResponseEntity(tasks, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
