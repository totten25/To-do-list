package com.todolist.task.exception;

public class TaskException extends Exception {
	private static final long serialVersionUID = -838141674973326638L;
	private String errorMessage;
	
	public TaskException() {
		super();
	}
	
	public TaskException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
}
