package com.fin.model.logs;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "auditlog")
public class AuditLog implements Serializable {
  
	private static final long serialVersionUID = 4492840233L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)	
	private Long logId;
	
	private String lineItem;
	
	
	private Long reporter;
	
	private String sourceDoc;
	
	private String action;
		
//	private String data;
//	

		
		
	public Long getLogId()
		{
		return logId;
		}



	public void setLogId(Long logId)
		{
		this.logId = logId;
		}



	public String getLineItem()
		{
		return lineItem;
		}



	public void setLineItem(String lineItem)
		{
		this.lineItem = lineItem;
		}


	public String getSourceDoc()
		{
		return sourceDoc;
		}



	public void setSourceDoc(String sourceDoc)
		{
		this.sourceDoc = sourceDoc;
		}



	public String getAction()
		{
		return action;
		}



	public void setAction(String action)
		{
		this.action = action;
		}



//	public String getData()
//		{
//		return data ;
//		}


//
//	public void setLogComment(String data)
//		{
//		this.data = data;
//		}



//	public static long getSerialversionuid()
//		{
//		return serialVersionUID;
//		}



	public Long getReporter() {
		return reporter;
	}



	public void setReporter(Long long1) {
		this.reporter = long1;
	}



//	public void setData(String data) {
//		this.data = data;
//	}


	@Column(name = "edited_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdOn;

	@PrePersist
    protected void prePersistFunction(){
		this.createdOn = new Date();
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}	
	
	
	

}
