package com.palak.springadvanceassignment.repository;

import com.palak.springadvanceassignment.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
// Spring Data JPA repository providing CRUD operations and database queries for todos
public interface TodoRepository extends JpaRepository<Todo, Long> {
}