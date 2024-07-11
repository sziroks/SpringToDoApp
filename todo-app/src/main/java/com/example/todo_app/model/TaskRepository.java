package com.example.todo_app.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
interface TaskRepository extends JpaRepository<Task, Integer> {

}
