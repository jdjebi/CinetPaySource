package com.payout.operatorservice.entity;

public class FinalStatusResponse {
	
	private String status;
	
	private String comment;
	
	public FinalStatusResponse(String status, String comment) {
		this.status = status;
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
