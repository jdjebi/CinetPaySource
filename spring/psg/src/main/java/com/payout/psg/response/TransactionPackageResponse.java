package com.payout.psg.response;

import com.payout.psg.core.TaskManager;

public class TransactionPackageResponse {
	
	private String message = "OK";
	private TaskManager taskManager;
	
	public TransactionPackageResponse(){
		
	}
	
	public TransactionPackageResponse(TaskManager taskManager){
		this.setTaskManager(taskManager);
	}
	
	public TransactionPackageResponse(String message, TaskManager taskManager){
		this.setMessage(message);
		this.setTaskManager(taskManager);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
}
