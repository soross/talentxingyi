package com.imo.global;

import java.util.HashMap;

/**
 * ÈÎÎñÀà
 */
public class IMOTask {

	private int taskType;

	private HashMap<String, Object> taskParams;

	public IMOTask() {
		super();
	}

	public IMOTask(int taskType, HashMap<String, Object> taskParams) {
		super();
		this.taskType = taskType;
		this.taskParams = taskParams;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public HashMap<String, Object> getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(HashMap<String, Object> taskParams) {
		this.taskParams = taskParams;
	}

}
