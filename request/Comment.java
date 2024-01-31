package com.fin.model.request;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long cmdId;
	private Long userId;
	private String comment;
	private Long requestId;
	private Long clientId;
	private long createdOn;
	
	Comment(){}
	
	public Comment(Long cmdId, Long userId, String comment, Long requestId, Long clientId, long createdOn) {
		super();
		this.cmdId = cmdId;
		this.userId = userId;
		this.comment = comment;
		this.requestId = requestId;
		this.clientId = clientId;
		this.createdOn = createdOn;
	}


	public Long getCmdId() {
		return cmdId;
	}


	public Long getUserId() {
		return userId;
	}


	public String getComment() {
		return comment;
	}


	public Long getRequestId() {
		return requestId;
	}


	public Long getClientId() {
		return clientId;
	}


	public long getCreatedOn() {
		return createdOn;
	}


	public void setCmdId(Long cmdId) {
		this.cmdId = cmdId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}


	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}


	@Override
	public String toString() {
		return "Comment [cmdId=" + cmdId + ", userId=" + userId + ", comment=" + comment + ", requestId=" + requestId
				+ ", clientId=" + clientId + ", createdOn=" + createdOn + "]";
	}
	
	
}
