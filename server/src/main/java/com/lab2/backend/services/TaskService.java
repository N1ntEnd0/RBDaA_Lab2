package com.lab2.backend.services;

import com.lab2.backend.entity.Tag;
import com.lab2.backend.entity.Task;
import com.lab2.backend.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {
    @Autowired
    private TasksRepository tasksRepository;

    public List<Task> getTasksByTag(String tag) {
        List<Task> taskList = this.tasksRepository.findAll();
        List<Task> resultList = new ArrayList<>();
        for (Task task : taskList) {
            for (Tag taskTag : task.getTags()) {
                if (Objects.equals(tag, taskTag.getLabel())) {
                    resultList.add(task);
                }
            }
        }
        return resultList;
    }

    public Task getLastTask() {
        List<Task> taskList = this.tasksRepository.findAll();
        return taskList.get(taskList.size() - 1);
    }

    public List<Task> getTasks() {
        return this.tasksRepository.findAll();
    }
}