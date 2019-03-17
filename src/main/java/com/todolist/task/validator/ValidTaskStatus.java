package com.todolist.task.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { TaskValidator.class })
public @interface ValidTaskStatus {
	public String value() default "";
	public String message() default "Invalid task status";
	public Class<?>[] groups() default {};
	public Class<? extends Payload>[] payload() default{};
}
