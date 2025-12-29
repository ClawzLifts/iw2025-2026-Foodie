package com.foodie.application.examplefeature;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task createTask(String description, LocalDate dueDate) {
        if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }
        if (description.length() > Task.DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("description too long");
        }
        Task t = new Task();
        t.setDescription(description);
        t.setDueDate(dueDate);
        t.setCreationDate(Instant.now());
        return repository.save(t);
    }

    public Page<Task> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
