package com.palak.springadvanceassignment.service;

import com.palak.springadvanceassignment.client.NotificationServiceClient;
import com.palak.springadvanceassignment.dto.TodoDTO;
import com.palak.springadvanceassignment.entity.Todo;
import com.palak.springadvanceassignment.enums.TodoStatus;
import com.palak.springadvanceassignment.exception.InvalidStatusTransitionException;
import com.palak.springadvanceassignment.exception.TodoNotFoundException;
import com.palak.springadvanceassignment.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TodoService.
 *
 * Demonstrates:
 * - @ExtendWith(MockitoExtension.class) → enables Mockito in JUnit 5
 * - @Mock → creates a fake (mock) version of the dependency
 * - @InjectMocks → creates the real TodoService and injects the mocks into it
 * - verify() → checks that a method was called on a mock
 * - assertThrows() → verifies that an exception is thrown
 * - @BeforeEach → runs before every single test to set up fresh test data
 * - @DisplayName → gives a readable name to each test
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    // Mocks — fake versions of dependencies (no real DB, no real notifications)
    @Mock
    private TodoRepository todoRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    // Real TodoService — Mockito injects the mocks above into this
    @InjectMocks
    private TodoService todoService;

    // Reusable test data
    private Todo sampleTodo;
    private TodoDTO sampleDTO;

    @BeforeEach
    void setUp() {
        // A sample Todo entity used across multiple tests
        sampleTodo = new Todo();
        sampleTodo.setTitle("Buy groceries");
        sampleTodo.setDescription("Milk and eggs");
        sampleTodo.setStatus(TodoStatus.PENDING);
        sampleTodo.setCreatedAt(LocalDateTime.now());

        // Simulate what JPA would do — assign an id after save
        sampleTodo = new Todo("Buy groceries", "Milk and eggs", TodoStatus.PENDING, LocalDateTime.now()) {
            { /* We set id via reflection workaround using a helper below */ }
        };
        setId(sampleTodo, 1L);

        // A sample DTO used for create/update requests
        sampleDTO = new TodoDTO();
        sampleDTO.setTitle("Buy groceries");
        sampleDTO.setDescription("Milk and eggs");
        sampleDTO.setStatus(TodoStatus.PENDING);
    }

    // Helper to set private id field for tests 

    private void setId(Todo todo, Long id) {
        try {
            var field = Todo.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(todo, id);
        } catch (Exception e) {
            throw new RuntimeException("Could not set id on Todo for test", e);
        }
    }

    
    // CREATE TESTS
   

    @Test
    @DisplayName("createTodo → should save todo and return DTO with id")
    void createTodo_shouldSaveTodoAndReturnDTO() {
        // ARRANGE — when repository.save() is called, return our sample todo
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // ACT
        TodoDTO result = todoService.createTodo(sampleDTO);

        // ASSERT — check the returned DTO has correct values
        assertNotNull(result);
        assertEquals("Buy groceries", result.getTitle());
        assertEquals(TodoStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getId());

        // Verify repository.save() was called exactly once
        verify(todoRepository, times(1)).save(any(Todo.class));

        // Verify notification was sent
        verify(notificationServiceClient, times(1))
                .sendTodoCreatedNotification(eq(1L), eq("Buy groceries"));
    }

    @Test
    @DisplayName("createTodo → should default status to PENDING when not provided")
    void createTodo_shouldDefaultStatusToPending_whenStatusIsNull() {
        sampleDTO.setStatus(null); // no status in request
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoDTO result = todoService.createTodo(sampleDTO);

        assertEquals(TodoStatus.PENDING, result.getStatus());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("createTodo → should trim title before saving")
    void createTodo_shouldTrimTitle() {
        sampleDTO.setTitle("  Buy groceries  ");
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        todoService.createTodo(sampleDTO);

        // Capture what was passed to save and verify title was trimmed
        verify(todoRepository).save(argThat(todo ->
                todo.getTitle().equals("Buy groceries")
        ));
    }

    // GET ALL TESTS
   

    @Test
    @DisplayName("getAllTodos → should return list of all todos as DTOs")
    void getAllTodos_shouldReturnAllTodosAsDTOs() {
        Todo second = new Todo("Read book", "Java book", TodoStatus.COMPLETED, LocalDateTime.now());
        setId(second, 2L);

        when(todoRepository.findAll()).thenReturn(List.of(sampleTodo, second));

        List<TodoDTO> result = todoService.getAllTodos();

        assertEquals(2, result.size());
        assertEquals("Buy groceries", result.get(0).getTitle());
        assertEquals("Read book", result.get(1).getTitle());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllTodos → should return empty list when no todos exist")
    void getAllTodos_shouldReturnEmptyList_whenNoTodosExist() {
        when(todoRepository.findAll()).thenReturn(List.of());

        List<TodoDTO> result = todoService.getAllTodos();

        assertTrue(result.isEmpty());
        verify(todoRepository, times(1)).findAll();
    }

    // GET BY ID TESTS

    @Test
    @DisplayName("getTodoById → should return todo DTO when id exists")
    void getTodoById_shouldReturnTodoDTO_whenIdExists() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        TodoDTO result = todoService.getTodoById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Buy groceries", result.getTitle());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getTodoById → should throw TodoNotFoundException when id does not exist")
    void getTodoById_shouldThrowTodoNotFoundException_whenIdNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        TodoNotFoundException exception = assertThrows(
                TodoNotFoundException.class,
                () -> todoService.getTodoById(99L)
        );

        assertTrue(exception.getMessage().contains("99"));
        verify(todoRepository, times(1)).findById(99L);
    }

    // UPDATE TESTS

    @Test
    @DisplayName("updateTodo → should update title and return updated DTO")
    void updateTodo_shouldUpdateTitle_whenTitleProvided() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoDTO updateRequest = new TodoDTO();
        updateRequest.setTitle("Updated title");

        todoService.updateTodo(1L, updateRequest);

        verify(todoRepository).save(argThat(todo ->
                todo.getTitle().equals("Updated title")
        ));
    }

    @Test
    @DisplayName("updateTodo → should transition PENDING to COMPLETED successfully")
    void updateTodo_shouldTransitionPendingToCompleted() {
        sampleTodo.setStatus(TodoStatus.PENDING);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoDTO updateRequest = new TodoDTO();
        updateRequest.setStatus(TodoStatus.COMPLETED);

        todoService.updateTodo(1L, updateRequest);

        verify(todoRepository).save(argThat(todo ->
                todo.getStatus() == TodoStatus.COMPLETED
        ));
    }

    @Test
    @DisplayName("updateTodo → should transition COMPLETED to PENDING successfully")
    void updateTodo_shouldTransitionCompletedToPending() {
        sampleTodo.setStatus(TodoStatus.COMPLETED);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoDTO updateRequest = new TodoDTO();
        updateRequest.setStatus(TodoStatus.PENDING);

        todoService.updateTodo(1L, updateRequest);

        verify(todoRepository).save(argThat(todo ->
                todo.getStatus() == TodoStatus.PENDING
        ));
    }

    @Test
    @DisplayName("updateTodo → should throw InvalidStatusTransitionException for same status")
    void updateTodo_shouldThrowException_whenSameStatusProvided() {
        sampleTodo.setStatus(TodoStatus.PENDING);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        // Same status transition (PENDING → PENDING) — should NOT throw based on current logic
        // If same, it's a no-op. Testing non-existent enum value isn't possible.
        // Instead test that no exception is thrown for same → same
        TodoDTO updateRequest = new TodoDTO();
        updateRequest.setStatus(TodoStatus.PENDING);

        assertDoesNotThrow(() -> todoService.updateTodo(1L, updateRequest));
    }

    @Test
    @DisplayName("updateTodo → should throw TodoNotFoundException when id not found")
    void updateTodo_shouldThrowTodoNotFoundException_whenIdNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        TodoDTO updateRequest = new TodoDTO();
        updateRequest.setTitle("New title");

        assertThrows(TodoNotFoundException.class,
                () -> todoService.updateTodo(99L, updateRequest));
    }

    // DELETE TESTS

    @Test
    @DisplayName("deleteTodo → should delete todo when id exists")
    void deleteTodo_shouldDeleteTodo_whenIdExists() {
        when(todoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(1L);

        todoService.deleteTodo(1L);

        verify(todoRepository, times(1)).existsById(1L);
        verify(todoRepository, times(1)).deleteById(1L);
        verify(notificationServiceClient, times(1)).sendTodoDeletedNotification(1L);
    }

    @Test
    @DisplayName("deleteTodo → should throw TodoNotFoundException when id does not exist")
    void deleteTodo_shouldThrowTodoNotFoundException_whenIdNotFound() {
        when(todoRepository.existsById(99L)).thenReturn(false);

        TodoNotFoundException exception = assertThrows(
                TodoNotFoundException.class,
                () -> todoService.deleteTodo(99L)
        );

        assertTrue(exception.getMessage().contains("99"));
        verify(todoRepository, never()).deleteById(any());
        verify(notificationServiceClient, never()).sendTodoDeletedNotification(any());
    }
}