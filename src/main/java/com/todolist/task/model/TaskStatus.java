package com.todolist.task.model;

import javax.validation.constraints.NotNull;

import com.todolist.task.constant.TaskConstant;
import com.todolist.task.validator.ValidTaskStatus;

public class TaskStatus {

	@NotNull(message=TaskConstant.Required.status)
	@ValidTaskStatus
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
