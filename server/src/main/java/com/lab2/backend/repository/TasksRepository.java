package com.lab2.backend.repository;

import com.lab2.backend.entity.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TasksRepository extends CrudRepository<Task, Long> {
    ArrayList<Task> findAll();
    ArrayList<Task> findAllByUserLogin(String login);
}
