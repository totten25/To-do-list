package com.todolist.task.constant;

public enum TaskConstant {
	;
	public enum Status {
		;
		public static final String pending = "pending";
		public static final String done = "done";
	}
	
	public enum Required {
		;
		public static final String subject = "subject is a required field";
		public static final String status = "status is a required field";
	}
	
	public enum Response {
		;
		public static final String success = "success";
		public static final String MSG_400 = "Parameters are mismatched";
		public static final String MSG_404 = "The resource you were trying to reach is not found";
		public static final String MSG_500 = "Interal server error";
	}
}
