package com.fin.model.request;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import com.fin.model.client.Client;
import com.fin.model.user.User;

@Entity
@Table(name = "requests")
public class ClientRequest implements Serializable {

	private static final long serialVersionUID = 12341439120L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long requestId;

	private long dueDate;

	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;

	@ManyToOne
	@JoinColumn(name = "reporter_id", nullable = false)
	private User reporter;

	String requestType;
	private String details;
	private String selectDatabase;
	private String selectEditType;
	private String selectLineItem;
//	private boolean status;
	//private String comment;

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getSelectDatabase() {
		return selectDatabase;
	}

	public void setSelectDatabase(String selectDatabase) {
		this.selectDatabase = selectDatabase;
	}

	public String getSelectEditType() {
		return selectEditType;
	}

	public void setSelectEditType(String selectEditType) {
		this.selectEditType = selectEditType;
	}

	public String getSelectLineItem() {
		return selectLineItem;
	}

	public void setSelectLineItem(String selectLineItem) {
		this.selectLineItem = selectLineItem;
	}

//	public String getComment() {
//		return comment;
//	}
//
//	public void setComment(String comment) {
//		this.comment = comment;
//	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public User getReporter() {
		return reporter;
	}

	public void setReporter(User reporter) {
		this.reporter = reporter;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

//	public boolean isStatus() {
//		return status;
//	}
//
//	public void setStatus(boolean status) {
//		this.status = status;
//	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 15)
	private Status status = Status.newRequest;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void markInProgress() {
		this.status = Status.inProgress;
	}

	public void markCompleted() {
		this.status = Status.completed;
	}

	public void markRejected() {
		this.status = Status.rejected;

	}

	public void markClossed() {
		this.status = Status.closed;

	}
	 
	public void markSubmitted() {
		this.status = Status.submitted;
	}
	
	public void markDeleted() {
		this.status = Status.deleted;
	}
	

	@Override
	public String toString() {
		return "ClientRequest [requestId=" + requestId + ", dueDate=" + dueDate + ", client=" + client + ", reporter="
				+ reporter + ", requestType=" + requestType + ", details=" + details + ", selectDatabase="
				+ selectDatabase + ", selectEditType=" + selectEditType + ", selectLineItem=" + selectLineItem
				+ ", status=" + status +  ", editedOn=" + editedOn + ", createdOn=" + createdOn
				+ "]";
	}

	public ClientRequest(Long requestId, long dueDate, Client client, User reporter, String requestType, String details,
			String selectDatabase, String selectEditType, String selectLineItem,  Status status,
			long editedOn, long createdOn) {
		super();
		this.requestId = requestId;
		this.dueDate = dueDate;
		this.client = client;
		this.reporter = reporter;
		this.requestType = requestType;
		this.details = details;
		this.selectDatabase = selectDatabase;
		this.selectEditType = selectEditType;
		this.selectLineItem = selectLineItem;
//		this.comment = comment;
//		this.status = status;
		this.editedOn = editedOn;
		this.createdOn = createdOn;
	}

	public ClientRequest() {
		super();
	}

	@Column(columnDefinition = "bigint default 0")
	private long editedOn;

	@Column(columnDefinition = "bigint default 0")
	private long createdOn;

//	Instant now = Instant.now();
//	Duration duration = Duration.ofDays(30);
//	Instant dd = now.plus(duration);

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
		this.editedOn = System.currentTimeMillis();

		Instant now = Instant.now();
		Duration duration = Duration.ofDays(30);
		Instant dd = now.plus(duration);
		this.dueDate = dd.toEpochMilli();

		// Check if dueDate is in the past and update status to "Closed"
		if (this.dueDate <= System.currentTimeMillis()) {
			this.status = Status.closed;
			////////////////////////////
			// this.dueDate = dd.toEpochMilli();
		}

	}

	@PreUpdate
	protected void preUpdateFunction() {
		this.editedOn = System.currentTimeMillis();
	}

	public long getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(long editedOn) {
		this.editedOn = editedOn;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

//	public Duration getDuration() {
//		return duration;
//	}
//
//	public void setDuration(Duration duration) {
//		this.duration = duration;
//	}

	private transient String massage;
//	private String massage;

	public String getMassage() {
		if (this.getStatus() == Status.closed) {
			Instant closureInstant = Instant.ofEpochMilli(this.getDueDate());
			LocalDateTime closureDateTime = closureInstant.atZone(ZoneOffset.UTC).toLocalDateTime();
			LocalDateTime currentDateTime = LocalDateTime.now();

			Duration duration = Duration.between(closureDateTime, currentDateTime);
			long years = duration.toDays() / 365; // Calculate years
			long months = duration.toDays() / 30;
			long days = duration.toDays() % 30;
			long hours = duration.toHours() % 24;
//			long minutes = duration.toMinutes() % 60;

			StringBuilder message = new StringBuilder();
			if (years > 0) {
				message.append(years).append(" year").append(years > 1 ? "s" : "");
			}
			if (months > 0) {
				message.append(months).append(" month").append(months > 1 ? "s" : "");
			}
			if (days > 0) {
				if (message.length() > 0) {
					message.append(", ");
				}
				message.append(days).append(" day").append(days > 1 ? "s" : "");
			}
			if (hours > 0) {
				if (message.length() > 0) {
					message.append(", ");
				}
				message.append(hours).append(" hour").append(hours > 1 ? "s" : "");
			}
//			if (minutes > 0) {
//				if (message.length() > 0) {
//					message.append(", ");
//				}
//				message.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
//			}

			if (message.length() == 0) {
				return "Less than a minute ago";
			} else {
				this.massage = message.toString() + " ago";
				return this.massage;
			}
		}
		return this.massage;
	}

}
