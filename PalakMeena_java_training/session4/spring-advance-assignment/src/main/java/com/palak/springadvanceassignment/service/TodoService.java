package com.palak.springadvanceassignment.service;

import com.palak.springadvanceassignment.client.NotificationServiceClient;
import com.palak.springadvanceassignment.dto.TodoDTO;
import com.palak.springadvanceassignment.entity.Todo;
import com.palak.springadvanceassignment.enums.TodoStatus;
import com.palak.springadvanceassignment.exception.InvalidStatusTransitionException;
import com.palak.springadvanceassignment.exception.TodoNotFoundException;
import com.palak.springadvanceassignment.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
// Business logic layer for todo operations: CRUD operations, status validation,
// and DTO mapping
public class TodoService {

    // Logger for tracking what happens inside this service
    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;
    private final NotificationServiceClient notificationServiceClient;

    // Both dependencies are injected via constructor - no @Autowired needed
    public TodoService(TodoRepository todoRepository,
            NotificationServiceClient notificationServiceClient) {
        this.todoRepository = todoRepository;
        this.notificationServiceClient = notificationServiceClient;
    }

    // Creates a new todo with default PENDING status if not specified
    public TodoDTO createTodo(TodoDTO dto) {
        log.info("Creating new todo with title: '{}'", dto.getTitle());

        Todo todo = new Todo();
        todo.setTitle(dto.getTitle().trim());
        todo.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);

        todo.setStatus(dto.getStatus() != null ? dto.getStatus() : TodoStatus.PENDING);

        // createdAt is always set by the server, never taken from the request
        todo.setCreatedAt(LocalDateTime.now());

        Todo saved = todoRepository.save(todo);
        log.info("Todo created successfully with id: {}", saved.getId());

        // Notify the external service that a new todo was created
        notificationServiceClient.sendTodoCreatedNotification(saved.getId(), saved.getTitle());

        return mapToDTO(saved);
    }

    // Fetches all todos from the database
    public List<TodoDTO> getAllTodos() {
        log.info("Fetching all todos");
        List<TodoDTO> todos = todoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        log.info("Found {} todo(s)", todos.size());
        return todos;
    }

    // Retrieves a specific todo by ID or throws exception if not found
    public TodoDTO getTodoById(Long id) {
        log.info("Fetching todo with id: {}", id);
        Todo todo = findByIdOrThrow(id);
        return mapToDTO(todo);
    }

    // Updates todo properties and validates status transitions
    public TodoDTO updateTodo(Long id, TodoDTO dto) {
        log.info("Updating todo with id: {}", id);
        Todo existing = findByIdOrThrow(id);

        // Update title if provided
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            existing.setTitle(dto.getTitle().trim());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription().trim());
        }

        if (dto.getStatus() != null) {
            validateStatusTransition(existing.getStatus(), dto.getStatus());
            existing.setStatus(dto.getStatus());
        }

        Todo updated = todoRepository.save(existing);
        log.info("Todo updated successfully with id: {}", updated.getId());
        return mapToDTO(updated);
    }

    // Removes a todo from the database
    public void deleteTodo(Long id) {
        log.info("Deleting todo with id: {}", id);
        if (!todoRepository.existsById(id)) {
            log.warn("Todo not found with id: {}", id);
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
        log.info("Todo deleted successfully with id: {}", id);

        // Let the notification service know this todo was removed
        notificationServiceClient.sendTodoDeletedNotification(id);
    }

    // Finds a todo by ID or throws TodoNotFoundException
    private Todo findByIdOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No todo found with id: {}", id);
                    return new TodoNotFoundException(id);
                });
    }

    // Validates that status transitions are only PENDING <-> COMPLETED
    private void validateStatusTransition(TodoStatus from, TodoStatus to) {
        if (from == to) {
            return;
        }
        boolean isAllowed = (from == TodoStatus.PENDING && to == TodoStatus.COMPLETED)
                || (from == TodoStatus.COMPLETED && to == TodoStatus.PENDING);

        if (!isAllowed) {
            log.warn("Invalid status transition attempted: {} -> {}", from, to);
            throw new InvalidStatusTransitionException(from, to);
        }
    }

    // Manually maps a Todo entity to a TodoDTO - no MapStruct or Lombok used
    private TodoDTO mapToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        dto.setCreatedAt(todo.getCreatedAt());
        return dto;
    }
}