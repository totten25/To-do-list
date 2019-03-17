package com.todolist.task.controllers;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.task.constant.TaskConstant;
import com.todolist.task.entities.Task;
import com.todolist.task.exception.TaskException;
import com.todolist.task.model.SuccessResponse;
import com.todolist.task.model.TaskStatus;
import com.todolist.task.services.TaskService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value="To-do-list Management")
public class TaskController extends BaseController {

	@Autowired
	TaskService taskService;
	
	@ApiOperation(value = "View all items in the list")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retrieve list are success"),
			@ApiResponse(code = 404, message = TaskConstant.Response.MSG_404),
			@ApiResponse(code = 500, message = TaskConstant.Response.MSG_500)
	})
	@GetMapping("/tasks")
	private ResponseEntity<List<Task>> getAllTasks() {
		return new ResponseEntity<List<Task>>(taskService.getAllTasks(), HttpStatus.OK);
	}
	
	@ApiOperation(value = "View a single task in the list")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retrieve an item is success"),
			@ApiResponse(code = 404, message = TaskConstant.Response.MSG_404),
			@ApiResponse(code = 500, message = TaskConstant.Response.MSG_500)
	})
	@GetMapping("/tasks/{id}")
	private ResponseEntity<Task> getTask(@PathVariable int id) throws TaskException {
		return new ResponseEntity<Task>(taskService.getTaskById(id), HttpStatus.OK);
	}
	
	@ApiOperation(value = "Add a task to the list")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Add an item is success"),
			@ApiResponse(code = 400, message = TaskConstant.Response.MSG_400),
			@ApiResponse(code = 404, message = TaskConstant.Response.MSG_404),
			@ApiResponse(code = 500, message = TaskConstant.Response.MSG_500)
	})
	@PostMapping("/tasks")
	private ResponseEntity<SuccessResponse> saveTask(@Valid @RequestBody Task task) throws TaskException {
		taskService.save(task);
		return new ResponseEntity<SuccessResponse>(new SuccessResponse(TaskConstant.Response.success), HttpStatus.OK);
	}
	
	@ApiOperation(value = "Edit an existing task")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Edit an item is success"),
			@ApiResponse(code = 400, message = TaskConstant.Response.MSG_400),
			@ApiResponse(code = 404, message = TaskConstant.Response.MSG_404),
			@ApiResponse(code = 500, message = TaskConstant.Response.MSG_500)
	})
	@PutMapping("/tasks/{id}")
	private ResponseEntity<SuccessResponse> updateTask(@Valid @RequestBody Task task, @PathVariable int id) throws TaskException {
		taskService.update(id, task);
		return new ResponseEntity<SuccessResponse>(new SuccessResponse(TaskConstant.Response.success), HttpStatus.OK);
	}
	
	@ApiOperation(value = "Set the task status")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Edit an item is success"),
			@ApiResponse(code = 400, message = TaskConstant.Response.MSG_400),
			@ApiResponse(code = 404, message = TaskConstant.Response.MSG_404),
			@ApiResponse(code = 500, message = TaskConstant.Response.MSG_500)
	})
	@PatchMapping("/tasks/{id}")
	private ResponseEntity<SuccessResponse> updateTaskStatus(@Valid @RequestBody TaskStatus taskStatus, @PathVariable int id) throws TaskException {
		taskService.updateTaskStatus(taskStatus.getStatus(), id);
		return new ResponseEntity<SuccessResponse>(new SuccessResponse(TaskConstant.Response.success), HttpStatus.OK);
	}

	@ApiOperation(value = "Delete a task from the list")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Remove an item is success"),
			@ApiResponse(code = 404, message = TaskConstant.Response.MSG_404),
			@ApiResponse(code = 500, message = TaskConstant.Response.MSG_500)
	})
	@DeleteMapping("/tasks/{id}")
	private ResponseEntity<SuccessResponse> deleteTask(@PathVariable int id) throws TaskException {
		taskService.delete(id);
		return new ResponseEntity<SuccessResponse>(new SuccessResponse(TaskConstant.Response.success), HttpStatus.OK);
	}
}
