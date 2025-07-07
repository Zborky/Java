package com.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves all tasks from the database.
     *
     * @return a list of all tasks
     */
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return the task with the specified ID
     * @throws java.util.NoSuchElementException if the task is not found
     */
    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow();
    }

    /**
     * Creates a new task and sets the creation timestamp.
     *
     * @param task the task to create
     * @return the created task
     */
    public Task createTask(Task task){
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    /**
     * Updates an existing task with new information.
     *
     * @param id the ID of the task to update
     * @param updatedTask the updated task data
     * @return the updated task
     * @throws java.util.NoSuchElementException if the task is not found
     */
    public Task updateTask(Long id, Task updatedTask){
        Task task = getTaskById(id);
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());
        return taskRepository.save(task);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     */
    public void deleteTask(Long id){
        taskRepository.deleteById(id);
    }
}
