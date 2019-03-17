package com.todolist.task.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.todolist.task.constant.TaskConstant;

public class TaskValidator implements ConstraintValidator<ValidTaskStatus, String> {
			
	private boolean checkTaskStatus(String status) {
		if (status.equalsIgnoreCase(TaskConstant.Status.pending))
			return true;
		else if (status.equalsIgnoreCase(TaskConstant.Status.done))
			return true;
		else 
			return false;
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value != null)
			return checkTaskStatus(value);
		else 
			return false;
	}

}
