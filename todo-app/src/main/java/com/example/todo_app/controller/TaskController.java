package com.example.todo_app.controller;

import com.example.todo_app.model.Task;
import com.example.todo_app.model.TaskRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@Controller
class TaskController {
    private final TaskRepository repository;
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    TaskController(final TaskRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks", params = {"!sort", "!page", "!size"})
    ResponseEntity<List<Task>> readAllTasks() {
        logger.info("Exposing all the tasks");
        return ResponseEntity.ok(repository.findAll());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable page) {
        logger.info("Custom pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks/{id}")
    ResponseEntity<Task> readTaskById(@PathVariable("id") int id) {
        logger.info("Exposing task, id: {}", id);
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/tasks")
    ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        logger.info("Creating task: {}", task.toString());
        if (repository.findById(task.getId()).isPresent()) {
            logger.info("Task with id {} already exists", task.getId());
            return ResponseEntity.badRequest().build();
        }
        try {
            Task result = repository.save(task);
            logger.info("Task {} created successfully", task.toString());
            return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
        } catch (Exception e) {
            logger.error("Task {} not created: {}", task.toString(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/tasks/{id}")
    ResponseEntity<Task> updateTask(@PathVariable("id") int id, @RequestBody @Valid Task toUpdate) {
        logger.info("Replacing task, id: {}", id);
        if (!repository.existsById(id)) {
            logger.info("Replaced task, id: {}", id);
            return ResponseEntity.notFound().build();
        } else if (id != toUpdate.getId()) {
            logger.error("Task not replaced, id {} is not present in provided task", id);
            return ResponseEntity.badRequest().build();
        }
        toUpdate.setId(id);
        repository.save(toUpdate);
        return ResponseEntity.noContent().build();
    }
}