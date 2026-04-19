package com.palak.springadvanceassignment.service;

import com.palak.springadvanceassignment.dto.TodoDTO;
import com.palak.springadvanceassignment.entity.Todo;
import com.palak.springadvanceassignment.enums.TodoStatus;
import com.palak.springadvanceassignment.exception.InvalidStatusTransitionException;
import com.palak.springadvanceassignment.exception.TodoNotFoundException;
import com.palak.springadvanceassignment.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // CREATE

    public TodoDTO createTodo(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle().trim());
        todo.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);

        
        todo.setStatus(dto.getStatus() != null ? dto.getStatus() : TodoStatus.PENDING);

     
        todo.setCreatedAt(LocalDateTime.now());

        Todo saved = todoRepository.save(todo);
        return mapToDTO(saved);
    }

    // GET ALL

    public List<TodoDTO> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID 

    public TodoDTO getTodoById(Long id) {
        Todo todo = findByIdOrThrow(id);
        return mapToDTO(todo);
    }

    //  UPDATE 

    public TodoDTO updateTodo(Long id, TodoDTO dto) {
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
        return mapToDTO(updated);
    }

    //  DELETE 

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    // PRIVATE HELPERS 

    
    private Todo findByIdOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

  
    private void validateStatusTransition(TodoStatus from, TodoStatus to) {
        if (from == to) {
            return; 
        }
       
        boolean isAllowed = (from == TodoStatus.PENDING   && to == TodoStatus.COMPLETED)
                         || (from == TodoStatus.COMPLETED && to == TodoStatus.PENDING);

        if (!isAllowed) {
            throw new InvalidStatusTransitionException(from, to);
        }
    }

    
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