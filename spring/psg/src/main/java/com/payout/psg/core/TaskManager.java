package com.payout.psg.core;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
	private List<Task> tasks = null;
	
	private String test = null;
		
	public TaskManager() {
		this.tasks = new ArrayList<Task>();
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public void add(String message) {
		this.tasks.add(new Task(message));
	}
	
	public void add(String message, String data) {
		this.tasks.add( new Task(message, data));
	}
		
}
