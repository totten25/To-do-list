package com.todolist.task.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.todolist.task.constant.TaskConstant;
import com.todolist.task.validator.ValidTaskStatus;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

@Entity
public class Task {
	@Id
	@GeneratedValue
	@ApiModelProperty(value = "The database generated task ID (Please remove this key when you create item)", accessMode=AccessMode.READ_ONLY)
	private int id;
	@ApiModelProperty(value = "a subject of the task", required=true)
	@NotNull(message = TaskConstant.Required.subject)
	@NotEmpty(message = TaskConstant.Required.subject)
	private String subject;
	@ApiModelProperty(value = "a free text content or detail for the task")
	private String description;
	@ApiModelProperty(value = "a status of the task, whether it is pending or done", required=true)
	@NotNull(message = TaskConstant.Required.status)
	@ValidTaskStatus
	private String status;
	
	public Task() {
		
	}
	
	public Task(int id, String subject, String description, String status) {
		this.id = id;
		this.subject = subject;
		this.description = description;
		this.status = status;
	}
	
	public Task(String subject, String description, String status) {
		this.subject = subject;
		this.description = description;
		this.status = status;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status.toLowerCase();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
