package com.fin.model.request;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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

    @Column(name = "due_date")
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    private String requestType;
    private String selectDatabase;
    private String selectEditType;
    private String selectLineItem;
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private Status status = Status.NEW;

    @Column(name = "edited_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date editedOn;

    @Column(name = "created_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdOn;

    @PrePersist
    protected void prePersistFunction() {
        Calendar calendar = Calendar.getInstance();
      //  calendar.set(Calendar.DAY_OF_MONTH, 30);
       calendar.add(Calendar.MONTH, 1);
        this.dueDate = calendar.getTime();
        this.createdOn = new Date();
        this.editedOn = new Date();
    }
      

    public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}


	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}


	@PreUpdate
    protected void preUpdateFunction() {
        this.editedOn = new Date();
    }

    public ClientRequest() {
        super();
        this.status = Status.NEW; // Set the default status to NEW
    }
	public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

//    public String getFormattedDueDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
//        return dateFormat.format(dueDate);
//    }

   

    public Client getClient() {
        return client;
    }

    public Date getDueDate() {
		return dueDate;
	}


	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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


	public String getDetails() {
		return details;
	}


	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}


	public void setDetails(String details) {
		this.details = details;
	}


	public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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


    public Date getEditedOn() {
        return editedOn;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void markInProgress() {
        this.status = Status.IN_PROGRESS;
    }

    public void markCompleted() {
        this.status = Status.COMPLETED;
    }

    public void markRejected() {
        this.status = Status.REJECTED;
        
    }

    @Column(name = "comment", length = 500) // Adjust length as needed
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ClientRequest [requestId=" + requestId + ", dueDate=" + dueDate + ", client=" + client + ", reporter="
                + reporter + ", requestType=" + requestType + ", selectDatabase=" + selectDatabase + ", selectEditType="
                + selectEditType + ", selectLineItem=" + selectLineItem + ", details=" + details + ", status=" + status
                + ", comment=" + comment + ", editedOn=" + editedOn + ", createdOn=" + createdOn + "]";
    }

}



