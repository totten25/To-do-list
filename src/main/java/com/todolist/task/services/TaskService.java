package com.todolist.task.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todolist.task.entities.Task;
import com.todolist.task.exception.TaskException;
import com.todolist.task.repositories.TaskRepository;

@Service
@Transactional(rollbackFor = TaskException.class)
public class TaskService {
	
	@Autowired
	TaskRepository taskRepository;
	
	public List<Task> getAllTasks() {
		return (List<Task>) taskRepository.findAll();
	}
	
	public Task getTaskById(int id) throws TaskException {
		Optional<Task> optionalTask = taskRepository.findById(id);
		if (!optionalTask.isPresent())
			throw new TaskException("Not found id: " + id);
		else
			return optionalTask.get();
	}
	
	private Optional<Task> findTaskBySubject(String subject) throws TaskException {
		Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findTaskBySubject(subject));
		return optionalTask;
	}
	
	private Optional<Task> findDuplicateTaskBySubjectAndId(String subject, int id) throws TaskException {
		Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findDuplicateTaskBySubjectAndId(subject, id));
		return optionalTask;
	}
	
	public Task save(Task task) throws TaskException {
		Optional<Task> optionalTask = findTaskBySubject(task.getSubject());
		if (optionalTask.isPresent())
			throw new TaskException("Task subject["+task.getSubject()+"] has already existed"); 
		return taskRepository.save(task); 
	}
	
	public Task update(int id, Task task) throws TaskException {
		Optional<Task> optionalTask = findDuplicateTaskBySubjectAndId(task.getSubject(), id);
		if (optionalTask.isPresent())
			throw new TaskException("Task subject["+task.getSubject()+"] has already existed");
		Task updateTask = getTaskById(id);
		updateTask.setSubject(task.getSubject());
		updateTask.setDescription(task.getDescription());
		updateTask.setStatus(task.getStatus());
		taskRepository.save(updateTask);
		return updateTask;
	}
	
	public int updateTaskStatus(String status, int id) throws TaskException {
		return taskRepository.updateTaskStatusById(status, id);
	}
	
	public void delete(int id) throws TaskException {
		taskRepository.deleteById(id);
	}
	
}
