package com.todolist.task.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.todolist.task.entities.Task;

@Transactional
public interface TaskRepository extends CrudRepository<Task, Integer> {

	@Query("SELECT t from Task t where lower(t.subject) = lower(?1)")
	Task findTaskBySubject(String subject);
	
	@Query("SELECT t from Task t where lower(t.subject) = lower(?1) and t.id != (?2)")
	Task findDuplicateTaskBySubjectAndId(String subject, int id);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Task SET status=lower(?1) where id=(?2)")
	int updateTaskStatusById(String status, int id);
	
}
