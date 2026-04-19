package com.palak.springadvanceassignment.controller;

import com.palak.springadvanceassignment.dto.ApiResponse;
import com.palak.springadvanceassignment.dto.TodoDTO;
import com.palak.springadvanceassignment.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // POST /todos
    @PostMapping
    public ResponseEntity<ApiResponse<TodoDTO>> createTodo(
            @RequestBody @Valid TodoDTO dto) {

        TodoDTO created = todoService.createTodo(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Todo created successfully.", created));
    }

    // GET /todos
    @GetMapping
    public ResponseEntity<ApiResponse<List<TodoDTO>>> getAllTodos() {
        List<TodoDTO> todos = todoService.getAllTodos();
        return ResponseEntity.ok(
                ApiResponse.ok("All todos fetched successfully.", todos)
        );
    }

    // GET /todos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoDTO>> getTodoById(
            @PathVariable Long id) {

        TodoDTO todo = todoService.getTodoById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Todo fetched successfully.", todo)
        );
    }

    // PUT /todos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoDTO>> updateTodo(
            @PathVariable Long id,
            @RequestBody TodoDTO dto) {

      
        TodoDTO updated = todoService.updateTodo(id, dto);
        return ResponseEntity.ok(
                ApiResponse.ok("Todo updated successfully.", updated)
        );
    }

    // DELETE /todos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @PathVariable Long id) {

        todoService.deleteTodo(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Todo deleted successfully.", null)
        );
    }
}
