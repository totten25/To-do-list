package com.todolist.task.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.todolist.task.constant.TaskConstant;
import com.todolist.task.entities.Task;
import com.todolist.task.exception.TaskException;
import com.todolist.task.model.ErrorResponse;
import com.todolist.task.model.SuccessResponse;
import com.todolist.task.model.TaskStatus;
import com.todolist.task.services.TaskService;

public class TaskControllerTest extends AbstractControllerTest<TaskController, ExceptionController> {

	@Mock
	private TaskService taskService;
	
	@InjectMocks
	private TaskController taskController;
	
	private final Task mockSingleTask = new Task(1,"Sweep the floor", "both of 1st and 2nd floor", TaskConstant.Status.pending);
	
	@Before
	public void setup() {
		super.setup(taskController, new ExceptionController());
	}
	
	@Test
	public void testAPI_NotFound() throws Exception {
		// execute task controller
		MvcResult mvcResult = mvc.perform(get("/task_temp")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		// check status is 404 (NOT_FOUND)
		assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void getAllTaskList() throws Exception {
		// Initial mock up task
		String pendingStatus = TaskConstant.Status.pending;
		List<Task> mockTasks = Arrays.asList(
				this.mockSingleTask,
				new Task(2, "do homework", "mathematics", pendingStatus));
		// mock task service
		when(taskService.getAllTasks()).thenReturn(mockTasks);
		// execute task controller
		MvcResult mvcResult = mvc.perform(get("/tasks")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		// check status is ok
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		Task[] taskList = super.convertMVCResultToMap(mvcResult, Task[].class);
		// check task is not null, not empty, and task properties as same as mock data
		assertThat(taskList).isNotNull().isNotEmpty()
				.anyMatch(t -> t.getId() == 2)
				.anyMatch(t -> t.getSubject().equals("do homework"))
				.anyMatch(t -> t.getDescription().equals("mathematics"))
				.anyMatch(t -> t.getStatus().equals(pendingStatus));
		// check size equals 2
		assertTrue(taskList.length == mockTasks.size());
		// verify getAllTasks method is executed only 1 time
		verify(taskService, times(1)).getAllTasks();
	}
	
	@Test
	public void getTaskById_Found() throws Exception {
		// Initial mock up task
		int taskId = 1;
		// mock task service
		when(taskService.getTaskById(taskId)).thenReturn(this.mockSingleTask);
		// execute task controller
		MvcResult mvcResult = mvc.perform(get("/tasks/" + taskId)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		// check status is ok
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		Task task = super.convertMVCResultToMap(mvcResult, Task.class);
		// check task is not null, and task properties as same as mock data
		assertThat(task).isNotNull()
			.matches(t -> t.getId() == 1)
			.matches(t -> t.getSubject().equals(this.mockSingleTask.getSubject()))
			.matches(t -> t.getDescription().equals(this.mockSingleTask.getDescription()))
			.matches(t -> t.getStatus().equals(this.mockSingleTask.getStatus()));
		// verify getTaskById is executed only 1 time with id as parameters
		verify(taskService, times(1)).getTaskById(taskId);
	}
	
	@Test
	public void getTaskById_NotFound() throws Exception {
		// Initial mock up task
		int taskId =2;
		String errorMessage = "Not found id: " + taskId;
		// mock task service
		when(taskService.getTaskById(taskId)).thenThrow(new TaskException(errorMessage));
		// execute task controller
		MvcResult mvcResult = mvc.perform(get("/tasks/" + taskId)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		// check status is interval server error
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
		// check error is matched
		ErrorResponse errorResponse = super.convertMVCResultToMap(mvcResult, ErrorResponse.class);
		assertThat(errorResponse).isNotNull()
			.matches(t -> t.getMessage().equals("Unexpected error : " + errorMessage));
		// verify getTaskById is executed only 1 time with id = taskId as parameters
		verify(taskService, times(1)).getTaskById(taskId);
		// verify getTaskById never be executed with id = 1 as parameters
		verify(taskService, never()).getTaskById(1);
	}
	
	@Test
	public void createTask_Success() throws Exception {
		// initial mock up task
		Task task = new Task("work", "at home", TaskConstant.Status.pending);
		// execute task controller
		MvcResult mvcResult = mvc.perform(post("/tasks")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(task))).andReturn();
		// check status is ok
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		// check data is matched
		SuccessResponse successResponse = super.convertMVCResultToMap(mvcResult, SuccessResponse.class);
		assertEquals(TaskConstant.Response.success, successResponse.getMessage());
		// verify save method is executed only 1 time with mockSingleTask as parameter
		verify(taskService, times(1)).save(any(Task.class));
	}
	
	@Test
	public void createTask_AlreadyExisted() throws Exception {
		// initial mock up task
		String errorMessage = "Task subject["+this.mockSingleTask.getSubject()+"] has already existed";
		Task taskDuplicateSubject = new Task(this.mockSingleTask.getSubject(), "at home", TaskConstant.Status.pending);
		// mock task service
		when(taskService.save(any(Task.class))).thenThrow(new TaskException(errorMessage));
		// execute task controller
		MvcResult mvcResult = mvc.perform(post("/tasks")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(taskDuplicateSubject))).andReturn();
		// check status is internal server error
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
		// check error message is matched
		ErrorResponse errorResponse = super.convertMVCResultToMap(mvcResult, ErrorResponse.class);
		assertThat(errorResponse).isNotNull()
			.matches(t -> t.getMessage().equals("Unexpected error : " + errorMessage));
		// check save method is executed only 1 time
		verify(taskService, times(1)).save(any(Task.class));
	}
	
	@Test
	public void createTask_subject_invalid() throws Exception {
		// initial mock up task
		String errorMessage = "subject is a required field";
		Task newTask = new Task("", "at home", TaskConstant.Status.pending);
		// execute task controller
		MvcResult mvcResult = mvc.perform(post("/tasks")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(newTask))).andReturn();
		// check status is bad request
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), status);
		// check error mesage is matched
		ErrorResponse errorResponse = super.convertMVCResultToMap(mvcResult, ErrorResponse.class);
		assertThat(errorResponse).isNotNull()
			.matches(t -> t.getMessage().startsWith(errorMessage));
		// check save method never executed
		verify(taskService, never()).save(any(Task.class));
	}
	
	@Test
	public void createTask_status_invalid() throws Exception {
		// initial mock up task
		String errorMessage = "Invalid task status"; //subject is a required field
		Task newTask = new Task("clean living room", "at home", TaskConstant.Status.pending+"g");
		// execute task controller
		MvcResult mvcResult = mvc.perform(post("/tasks")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(newTask))).andReturn();
		// check status is bad request
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), status);
		// check error mesage is matched
		ErrorResponse errorResponse = super.convertMVCResultToMap(mvcResult, ErrorResponse.class);
		assertThat(errorResponse).isNotNull()
			.matches(t -> t.getMessage().startsWith(errorMessage));
		// check save method never executed
		verify(taskService, never()).save(any(Task.class));
	}
	
	@Test
	public void editTask_success() throws Exception {
		// initial mock up task
		int updateTaskId = this.mockSingleTask.getId();
		Task updatedTask = this.mockSingleTask;
		updatedTask.setSubject("Wash dishes");
		// execute task controller
		MvcResult mvcResult = mvc.perform(put("/tasks/" + updateTaskId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(updatedTask))).andReturn();
		// check status is OK
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		// check data is matched
		SuccessResponse successResponse = super.convertMVCResultToMap(mvcResult, SuccessResponse.class);
		assertEquals(TaskConstant.Response.success, successResponse.getMessage());
		// verify update method is executed only 1 time with mockSingleTask as parameter
		verify(taskService, times(1)).update(ArgumentMatchers.anyInt(), any(Task.class));
	}
	
	@Test
	public void editTask_subject_invalid() throws Exception {
		// initial mock up task
		String errorMessage = "subject is a required field";
		int updateTaskId = this.mockSingleTask.getId();
		Task updatedTask = this.mockSingleTask;
		updatedTask.setSubject("");
		// execute task controller
		MvcResult mvcResult = mvc.perform(put("/tasks/" + updateTaskId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(updatedTask))).andReturn();
		// check status is bad request
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), status);
		// check error mesage is matched
		ErrorResponse errorResponse = super.convertMVCResultToMap(mvcResult, ErrorResponse.class);
		assertThat(errorResponse).isNotNull()
			.matches(t -> t.getMessage().startsWith(errorMessage));
		// check update method never executed
		verify(taskService, never()).update(ArgumentMatchers.anyInt(), any(Task.class));
	}
	
	@Test
	public void updateTaskStatus_success() throws Exception {
		// initial mock up task
		int updateTaskId = this.mockSingleTask.getId();
		String doneStatus = TaskConstant.Status.done;
		TaskStatus taskStatus = new TaskStatus();
		taskStatus.setStatus(doneStatus);
		// execute task controller
		MvcResult mvcResult = mvc.perform(patch("/tasks/" + updateTaskId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(taskStatus))).andReturn();
		// check status is ok
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		// check data is matched
		SuccessResponse successResponse = super.convertMVCResultToMap(mvcResult, SuccessResponse.class);
		assertEquals(TaskConstant.Response.success, successResponse.getMessage());
		// verify updateTaskStatus method is executed only 1 time
		verify(taskService, times(1)).updateTaskStatus(doneStatus, updateTaskId);
	}
	
	@Test
	public void updateTaskStatus_invalid() throws Exception {
		// initial mock up task
		String errorMessage = "Invalid task status";
		int updateTaskId = this.mockSingleTask.getId();
		String doneStatus = TaskConstant.Status.done;
		TaskStatus taskStatus = new TaskStatus();
		taskStatus.setStatus(doneStatus+"gg");
		// execute task controller
		MvcResult mvcResult = mvc.perform(patch("/tasks/" + updateTaskId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(super.convertMapToJson(taskStatus))).andReturn();
		// check status is bad request
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), status);
		// check error mesage is matched
		ErrorResponse errorResponse = super.convertMVCResultToMap(mvcResult, ErrorResponse.class);
		assertThat(errorResponse).isNotNull()
			.matches(t -> t.getMessage().startsWith(errorMessage));
		// check updateTaskStatus method never be executed
		verify(taskService, never()).updateTaskStatus(doneStatus, updateTaskId);
	}
	
	@Test
	public void deleteTask_success() throws Exception {
		// initial mock up task
		int deleteTaskId = this.mockSingleTask.getId();
		// execute task controller
		MvcResult mvcResult = mvc.perform(delete("/tasks/" + deleteTaskId)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		// check status is ok
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		// check data is matched
		SuccessResponse successResponse = super.convertMVCResultToMap(mvcResult, SuccessResponse.class);
		assertEquals(TaskConstant.Response.success, successResponse.getMessage());
		// verify delete method is executed only 1 time
		verify(taskService, times(1)).delete(deleteTaskId);
	}
	
}
