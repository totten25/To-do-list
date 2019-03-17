package com.todolist.task.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.todolist.task.constant.TaskConstant;
import com.todolist.task.entities.Task;
import com.todolist.task.exception.TaskException;
import com.todolist.task.repositories.TaskRepository;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

	@InjectMocks
	TaskService taskService;
	
	@Mock
	TaskRepository taskRepository;
	
	private final Task mockSingleTask = new Task(1, "Clean the room", "Finished before 6 pm", TaskConstant.Status.pending);
	
	@Test
	public void getAllTasks() {
		// initial mock up task
		List<Task> mockupTaskList = new ArrayList<Task>();
		mockupTaskList.add(this.mockSingleTask);
		// mock task repository
		when(taskRepository.findAll()).thenReturn(mockupTaskList);
		// execute task service
		List<Task> list = taskService.getAllTasks();
		// check task is not null, not empty, and any task properties equals mock subject
		assertThat(list).isNotNull().isNotEmpty()
			.anyMatch(t -> t.getSubject().equals("Clean the room"))
			.anyMatch(t -> t.getDescription().equals("Finished before 6 pm"))
			.anyMatch(t -> t.getStatus().equals(TaskConstant.Status.pending));
		// check task size equals 1
		assertEquals(1, list.size());
		// check findAll method is executed only 1 time
		verify(taskRepository, times(1)).findAll();
	}
	
	@Test
	public void getTaskById_Found() throws TaskException {
		// initial mock up task
		int taskId = 1;
		Optional<Task> optionalTask = Optional.ofNullable(this.mockSingleTask);
		// mock task repository
		when(taskRepository.findById(taskId)).thenReturn(optionalTask);
		// execute task service
		Task task = taskService.getTaskById(taskId);
		// check task is not null, not empty, and any task properties equals mock subject
		assertThat(task).isNotNull()
			.matches(t -> t.getSubject().equals("Clean the room"))
			.matches(t -> t.getDescription().equals("Finished before 6 pm"))
			.matches(t -> t.getStatus().equals(TaskConstant.Status.pending));
		// check findById method is executed only 1 time with taskId = 1 as parameter
		verify(taskRepository, times(1)).findById(taskId);
		// check findById method never be executed with taskId = 2 as parameter
		verify(taskRepository, never()).findById(2);
	}
	
	@Test
	public void getTaskById_NotFound() throws TaskException {
		// initial mock up task
		int searchTaskId = 2;
		// mock task repository when taskId is existed
		when(taskRepository.findById(searchTaskId)).thenReturn(Optional.ofNullable(null));
		// check task is not found and then throw TaskException
		assertThatThrownBy(() -> taskService.getTaskById(searchTaskId))
			.isInstanceOf(TaskException.class);
		verify(taskRepository, times(1)).findById(searchTaskId);
	}

	@Test
	public void createTask_Success() throws TaskException {
		// initial mock up task
		Task newTask = new Task("Clean the room", "at home", TaskConstant.Status.done);
		// mock task repository
		when(taskRepository.save(newTask)).thenReturn(newTask);
		// check create task
		Task task = taskService.save(newTask);
		// check task is created successfully
		assertThat(task)
			.matches(t -> t.getSubject().equals(newTask.getSubject()))
			.matches(t -> t.getDescription().equals(newTask.getDescription()))
			.matches(t -> t.getStatus().equals(newTask.getStatus()));
		// check save method is executed only 1 time with mockSingleTask as parameter
		verify(taskRepository, times(1)).save(any(Task.class));
	}
	
	@Test
	public void createTask_AlreadyExisted() throws TaskException {
		// initial mock up task
		Task taskDuplicateSubject = new Task("Clean the room", "at home", TaskConstant.Status.done);
		// mock task repository
		when(taskRepository.findTaskBySubject("Clean the room")).thenReturn(this.mockSingleTask);
		// check create duplicate task subject and then throw exception
		assertThatThrownBy(() -> taskService.save(taskDuplicateSubject))
			.isInstanceOf(TaskException.class);
		// check findTaskBySubject method is executed only 1 time with subject "Clean the room" as parameter
		verify(taskRepository, times(1)).findTaskBySubject("Clean the room");
	}
	
	@Test
	public void editTask_Success() throws TaskException {
		// initial mock up task
		int updateTaskId = 1;
		Task updatedTask = new Task(updateTaskId, "Clean the floor", "at the office", TaskConstant.Status.pending);
		// mock task repository
		when(taskRepository.findById(updateTaskId)).thenReturn(Optional.ofNullable(this.mockSingleTask));
		// check update task
		Task task = taskService.update(updateTaskId, updatedTask);
		// check update task is finish
		assertThat(task).isNotNull()
			.matches(t -> t.getId() == updateTaskId)
			.matches(t -> t.getSubject().equals(updatedTask.getSubject()))
			.matches(t -> t.getDescription().equals(updatedTask.getDescription()))
			.matches(t -> t.getStatus().equals(updatedTask.getStatus()));
		// check findById method is executed only 1 time with updateTaskId, updatedTask as parameter
		verify(taskRepository, times(1)).findById(updateTaskId);
	}
	
	@Test
	public void editTask_NotFound() throws TaskException {
		// initial mock up task
		int updateTaskId = 2;
		Task updateTask = new Task("Meeting 17th math conference", "at konkaen university", TaskConstant.Status.done);
		// mock task repository
		when(taskRepository.findById(updateTaskId)).thenReturn(Optional.ofNullable(null));
		// check update task and then throw task exception
		assertThatThrownBy(() -> taskService.update(updateTaskId, updateTask))
			.isInstanceOf(TaskException.class);
		// check findById method is executed only 1 time 
		verify(taskRepository, times(1)).findById(updateTaskId);
	}
	
	@Test
	public void updateTaskStatusByTaskId_Success() throws TaskException {
		// initial mock up task
		int updateTaskId = 1;
		String doneStatus = TaskConstant.Status.done;
		Task updateTask = this.mockSingleTask;
		updateTask.setStatus(doneStatus);
		// mock task repository
		when(taskRepository.updateTaskStatusById(doneStatus, updateTaskId))
			.thenReturn(1);
		// update task status
		int numOfUpdatedRows = taskService.updateTaskStatus(doneStatus, updateTaskId); 
		// check associated rows are updated
		assertEquals(1, numOfUpdatedRows);
		// check updateTaskStatusById is executed only 1 time with status and id as parameters
		verify(taskRepository, times(1)).updateTaskStatusById(doneStatus, updateTaskId);
	}
	
	@Test
	public void deleteTask_Success() throws TaskException {
		// initial mock up task
		int deleteTaskId = 1;
		// mock task repository
		doNothing().when(taskRepository).deleteById(deleteTaskId);
		// delete task
		taskService.delete(deleteTaskId);
		// check deleteById is executed only 1 time with id as parameters
		verify(taskRepository, times(1)).deleteById(deleteTaskId);
	}
	
}
