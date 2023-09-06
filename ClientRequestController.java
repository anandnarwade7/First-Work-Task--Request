package com.fin.model.request;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Date;
import com.fin.model.logs.AuditLog;
import com.fin.model.logs.AuditLogRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@ExecuteOn(TaskExecutors.IO)
@Controller("/requests")
public class ClientRequestController {
	
	AuditLog al;
	protected final ClientRequestRepository repository;

	public ClientRequestController(ClientRequestRepository repos) {
		this.repository = repos;
		}
	
	@Get("/list")
	public List<ClientRequest> getAll() throws Exception{
		return (List<ClientRequest>) repository.findAll();
	}

	@Get
	public List<ClientRequest> getAllData() {
	    List<ClientRequest> requests = (List<ClientRequest>) repository.findAll();
	    
	    for (ClientRequest request : requests) {
	        try {
	            // Attempt to parse the Status field, set it to NEW if parsing fails
	            request.setStatus(Status.valueOf(request.getStatus().name()));
	        } catch (IllegalArgumentException e) {
	            request.setStatus(Status.NEW); // Set a default value
	        }
	    }
	
	   al.setAction("Fetched ClientRequests");
	    return requests;
	    
	}

	//To have Specific Request "for requester....."
	@Get("/myrequest/{id}")
	public Optional<ClientRequest> get(Long id) {

		
		al.setAction("Fetched ClientRequests by Ids"+id);
		return repository.findById(id);
	}

	
	//To Edit the Request.....
	
	@Put("/{id}")
	public ClientRequest update(Long id, @Body ClientRequest object) throws Exception {
		if (repository.existsById(id)) {
			object.setRequestId(id);
			repository.update(object);
			al.setAction("Edit ClientRequests by Id"+id);
			return object;
		} else {
			
			throw new Exception("Id Not Found");
		}
	}
	/////////////////
	@Put("/update/{id}")
	public ClientRequest update1(@QueryValue Long id, @Body ClientRequest object) throws Exception {
	    if (repository.existsById(id)) {
	        // Get the existing client request to retrieve reporter information
	        ClientRequest existingRequest = repository.findById(id).orElse(null);

	        if (existingRequest != null) {
	        	object.setCreatedOn(existingRequest.getCreatedOn());
                object.setDueDate(existingRequest.getDueDate());
                object.setEditedOn(new Date());
	            AuditLog auditLog = new AuditLog();
	            auditLog.setReporter(existingRequest.getReporter().getId());
//	            auditLog.setCreatedOn(existingRequest.getCreatedOn());
	            auditLog.setSourceDoc(existingRequest.getRequestType());
	            auditLog.setLineItem(existingRequest.getSelectLineItem());

	            // Update the action to indicate request edit
	            auditLog.setAction("Request with ID " + existingRequest.getRequestId() + " edited by "+existingRequest.getReporter().getName());

	            System.out.println(auditLog.getLogId());
	            System.out.println(auditLog.getAction());
	            System.out.println(auditLog.getLineItem());
	            System.out.println(auditLog.getSourceDoc());
	            System.out.println(auditLog.getCreatedOn());

	            alr.save(auditLog); // Save the audit log entry
	        }
	        object.setEditedOn(new Date());
	        object.setRequestId(id);
	        repository.update(object);

	        return object;
	    } else {
	        throw new Exception("Id Not Found");
	    }
	}
//////////////////////////////////////////////////////////////////////////////////////

	@Post
	public ClientRequest add(@Body ClientRequest object) {
	    
		return repository.save(object);
	}


	@Delete("/{id}")
	public void delete(Long id) throws Exception {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			al.setAction("Added ClientRequests"+id);
		} else {
			/*
			 * For demo purposes, I am using Generic Exception. In a real scenario you must
			 * implement custom exception handling.
			 */ throw new Exception("Id Not Found");
		}
	}
	///////////////////////////////////////////////
	@Delete("/delete/{id}")
	public void deleteq(@QueryValue Long id) throws Exception {
	    if (repository.existsById(id)) {
	        // Get the ClientRequest to retrieve reporter information
	        ClientRequest savedObject = repository.findById(id).orElse(null);

	        if (savedObject != null) {
	            AuditLog auditLog = new AuditLog();
	            auditLog.setReporter(savedObject.getReporter().getId());
	            auditLog.setSourceDoc(savedObject.getRequestType());
	            auditLog.setLineItem(savedObject.getSelectLineItem());

	            // Update the action to indicate request deletion
	            auditLog.setAction("Request with ID " + savedObject.getRequestId() + " deleted by "+savedObject.getReporter().getName());

	            System.out.println(auditLog.getLogId());
	            System.out.println(auditLog.getAction());
	            System.out.println(auditLog.getLineItem());
	            System.out.println(auditLog.getSourceDoc());
	            System.out.println(auditLog.getCreatedOn());

	            alr.save(auditLog); // Save the audit log entry
	        }

	        repository.deleteById(id);
	    } else {
	        throw new Exception("Id Not Found");
	    }
	}

	

	//to change status after reviewing request by admin
	@Put("/review/{id}")
    public ClientRequest reviewRequest(Long id) throws Exception {
        Optional<ClientRequest> optionalRequest = repository.findById(id);

        if (optionalRequest.isPresent()) {
            ClientRequest request = optionalRequest.get();

            if (request.getStatus() == Status.NEW) {
                request.markInProgress();
                repository.update(request);
                al.setAction("Added ClientRequests"+id);
                return request;
            } else {
                throw new Exception("Request is not in the 'New' status and cannot be reviewed.");
            }
        } else {
            throw new Exception("Request ID not found.");
        }
    }
	
	 
	//to change Status based on response
	 @Put("/review1/{id}")
	 public ClientRequest reviewRequest1(Long id, @QueryValue String response) throws Exception {
		    Optional<ClientRequest> optionalRequest = repository.findById(id);

		    if (optionalRequest.isPresent()) {
		        ClientRequest request = optionalRequest.get();

		        if ("yes".equalsIgnoreCase(response)) {
		            request.markCompleted();
		            repository.update(request);
		        } else if ("no".equalsIgnoreCase(response)) {
		            request.markRejected();
		            repository.update(request);
		        } else {
		            throw new Exception("Invalid response. Use 'yes' or 'no'.");
		        }

                al.setAction("Added ClientRequests"+id);
		        return request;
		    } else {
		        throw new Exception("Request ID not found.");
		    }
		}
	 
	 //To add Comment
	    @Put("/addComment/{id}")
	    public ClientRequest addCommentToRequest(@QueryValue Long id, @Body Map<String, String> requestBody) throws Exception {
	        Optional<ClientRequest> optionalRequest = repository.findById(id);

	        if (optionalRequest.isPresent()) {
	            ClientRequest request = optionalRequest.get();
	            String commentText = requestBody.get("commentText");
	            Date originalCreatedOn = request.getCreatedOn();
	            Date originalDueDate = request.getDueDate();
	            request.setComment(commentText);
	            request.setEditedOn(new Date());
	            request.setCreatedOn(originalCreatedOn);
	            request.setDueDate(originalDueDate);
	            repository.update(request);
	            AuditLog al = new AuditLog();
	            al.setReporter(request.getReporter().getId());
	            al.setSourceDoc(request.getRequestType());
	            al.setLineItem(request.getSelectLineItem());
	            al.setAction("Added comment to Request with ID " + request.getRequestId() + " by "
	                    + request.getReporter().getName());
	            alr.save(al);

	            return request;
	        } else {
	            throw new Exception("Request ID not found.");
	        }
	    }

	 @Inject
	 private AuditLogRepository alr;

//	 @Post("/add")
//	 public ClientRequest add2(@Body ClientRequest object) {
//	     ClientRequest savedObject = repository.save(object);
//	     AuditLog auditLog = new AuditLog();
//	     auditLog.setLogId(object.getRequestId());
//	     auditLog.setCreatedOn(object.getCreatedOn());
//	     auditLog.setSourceDoc(object.getRequestType());	
//	     auditLog.setLineItem(object.getSelectLineItem());
//	     auditLog.setAction("Request with ID " + savedObject.getReporter().getName() + " added.");
//	  System.out.println(auditLog.getLogId());   
//	  System.out.println(auditLog.getAction());
//	  System.out.println(auditLog.getLineItem());
//	  System.out.println(auditLog.getSourceDoc());
//	  System.out.println(auditLog.getCreatedOn());
//	     alr.save(auditLog);
//
//	     return savedObject;
//	 }
	 @Post("/add")
	 public ClientRequest add2(@Body ClientRequest object) {
	     ClientRequest savedObject = repository.save(object);
	     AuditLog auditLog = new AuditLog();
	     if (savedObject != null) {
	       //auditLog.setLogId(object.getRequestId());
	    	 auditLog.setReporter(object.getReporter().getId());
	         auditLog.setCreatedOn(object.getCreatedOn());
	         auditLog.setSourceDoc(object.getRequestType());
	         auditLog.setLineItem(object.getSelectLineItem());
	         if (savedObject.getReporter() != null && savedObject.getReporter().getId() != 0) {
	             auditLog.setAction("Request with ID " + savedObject.getRequestId()  + " added by"+savedObject.getReporter().getName());
	         } else {
	             auditLog.setAction("Request added, but reporter or reporter's name is null.");
	         }	        
	         System.out.println(auditLog.getLogId());
	         System.out.println(auditLog.getAction());
	         System.out.println(auditLog.getLineItem());
	         System.out.println(auditLog.getSourceDoc());
	         System.out.println(auditLog.getCreatedOn());
	         alr.save(auditLog);
	         
	     } else {
	         System.out.println("Saved object is null. Audit log not created.");
	     }

	     return savedObject;
	 }


//	 @Post("/add")
//	 public AuditLog initializeAction(@Body ClientRequest object) {
//	     AuditLog auditLog = new AuditLog();
//
//	     // Check if object is not null
//	     if (object != null) {
//	         auditLog.setLogId(object.getRequestId()); // Set logId to requestId
//
//	         // Check if object.getReporter() and getId() are not null
//	         if (object.getReporter() != null && object.getReporter().getId() != null) {
//	             auditLog.setAction("Request with ID " + object.getReporter().getId() + " added.");
//
//	             // Output logId to console
//	             System.out.println("Log ID: " + auditLog.getLogId());
//	         } else {
//	             // Handle the case where object.getReporter() or its id is null
//	             auditLog.setAction("Request added, but reporter or reporter's id is null.");
//	         }
//
//	         // Set other properties like lineItem, sourceDoc, etc.
//
//	         // Save the audit log entry
//	         alr.save(auditLog);
//
//	         return auditLog;
//	     } else {
//	         // Handle the case where object is null
//	         System.out.println("Received a null object. Audit log not created.");
//	         return null; // or handle default action as needed
//	     }
//	 }

}
	

//	@Post("/create")
//	public MutableHttpResponse<String> createClientRequest(@Body ClientRequest clientRequest) {
//        clientRequest.setDueDate(LocalDate.now().plusMonths(1).withDayOfMonth(28));
//        ClientRequest savedRequest = repository.save(clientRequest);
//        return HttpResponse.ok("ClientRequest created successfully with ID: " + savedRequest.getRequestId());
//    }

//	 private static String responseStatus = "Incomplete";
//
//		public static String getResponseStatus() {
//			return responseStatus;
//		}
//
//		public static void setResponseStatus(String responseStatus) {
//			ClientRequestController.responseStatus = responseStatus;
//		}
//	 @Put("/review1/{id}")
//	    public ClientRequest reviewRequest1(Long id) throws Exception {
//	        Optional<ClientRequest> optionalRequest = repository.findById(id);
//
//	        if (optionalRequest.isPresent()) {
//	            ClientRequest request = optionalRequest.get();
//
//	            if (request.getStatus() == Status.IN_PROGRESS) {
//	                if ("Incomplete".equals(responseStatus)) {
//	                    request.markCompleted();
//	                    repository.update(request);
//	                    responseStatus = "Complete";
//	                } else {
//	                    request.markRejected();
//	                    repository.update(request);
//	                    responseStatus = "Rejected";
//	                }
//	                return request;
//	            } else {
//	                throw new Exception("Request is not in the 'InProgress' status and cannot be reviewed.");
//	            }
//	        } else {
//	            throw new Exception("Request ID not found.");
//	        }
//	    }