package com.example.taskmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    /**
     * Retrieves all tasks.
     *
     * @return a list of all tasks
     */
    @GetMapping
    public List<Task> getAllTask(){
        return taskService.getAllTasks();
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task to retrieve
     * @return the task with the specified ID
     */
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    /**
     * Creates a new task.
     *
     * @param task the task to create
     * @return the created task
     */
    @PostMapping
    public Task createTask(@RequestBody Task task){
        return taskService.createTask(task);
    }

    /**
     * Updates an existing task by its ID.
     *
     * @param id the ID of the task to update
     * @param task the updated task data
     * @return the updated task
     */
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task){
        return taskService.updateTask(id, task);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
    }
}
