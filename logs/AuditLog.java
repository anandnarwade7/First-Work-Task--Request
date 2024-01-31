package com.fin.model.logs;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "auditlog")
public class AuditLog implements Serializable {
  
	private static final long serialVersionUID = 4492840233L;
	
	@Id
//    @GeneratedValue(strategy = GenerationType.AUTO)	
	private Long logId;
	
	private String lineItem;
	
	private String userRole;
		
	private String userEmail;
	
	private String userName;
	
	private String clientName;
	
	private String sourceDoc;
	
	private String action;
		
	private String data;
	
	@Column(columnDefinition = "bigint default 0")
	private long createdOn;

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getLineItem() {
		return lineItem;
	}

	public void setLineItem(String lineItem) {
		this.lineItem = lineItem;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getSourceDoc() {
		return sourceDoc;
	}

	public void setSourceDoc(String sourceDoc) {
		this.sourceDoc = sourceDoc;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "AuditLog [logId=" + logId + ", lineItem=" + lineItem + ", userRole=" + userRole + ", userEmail="
				+ userEmail + ", userName=" + userName + ", clientName=" + clientName + ", sourceDoc=" + sourceDoc
				+ ", action=" + action + ", data=" + data + ", createdOn=" + createdOn + "]";
	}

	public AuditLog(Long logId, String lineItem, String userRole, String userEmail, String userName, String clientName,
			String sourceDoc, String action, String data, long createdOn) {
		super();
		this.logId = logId;
		this.lineItem = lineItem;
		this.userRole = userRole;
		this.userEmail = userEmail;
		this.userName = userName;
		this.clientName = clientName;
		this.sourceDoc = sourceDoc;
		this.action = action;
		this.data = data;
		this.createdOn = createdOn;
	}

	public AuditLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	

	@PrePersist
    protected void prePersistFunction(){
		this.createdOn = System.currentTimeMillis() ;
	}

	
	

}
