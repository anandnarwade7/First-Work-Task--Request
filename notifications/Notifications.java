package com.fin.model.notifications;

import javax.persistence.*;

@Entity
public class Notifications {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String message;

	private String userName;
	

	public Notifications(){};

	public Notifications(long id, String message, String userName, boolean isSeen, long createdOn) {
		super();
		this.id = id;
		this.message = message;
		this.userName = userName;
		this.isSeen = isSeen;
		this.createdOn = createdOn;
	}

	public long getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getUserName() {
		return userName;
	}

	private boolean isSeen = false;
	
	public boolean isSeen() {
		return isSeen;
	}

	public void setSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}
	
	public long getCreatedOn() {
		return createdOn;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@Column(columnDefinition = "bigint default 0")
	private long createdOn;

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}


}
