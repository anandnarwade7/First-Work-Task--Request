package com.fin.model.logs;


import java.util.List;
import java.util.Optional;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

@ExecuteOn(TaskExecutors.IO)
@Controller("/auditlogs")

public class AuditLogController {
	//private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);
 
	protected final AuditLogRepository repository;

	public AuditLogController(AuditLogRepository repos) {
		this.repository = repos;
	}

	@Get
	public List<AuditLog> getAll() {
		return (List<AuditLog>) repository.findAll();
	}

	@Get("/{id}")
	public Optional<AuditLog> get(Long id) {
		return repository.findById(id);
	}

	@Put("/{id}")
	public AuditLog update(Long id, @Body AuditLog object) throws Exception {
		if (repository.existsById(id)) {
			object.setLogId(id);
			repository.update(object);
			return object;
		} else {
			/*
			 * For demo purposes, I am using Generic Exception. In a real scenario you must
			 * implement custom exception handling.
			 */ throw new Exception("Id Not Found");
		}
	}

	@Post
	public AuditLog add(@Body AuditLog object) {
		//System.out.println(object.getReporter().getId());
		return repository.save(object);
	}

	
	@Delete("/{id}")
	public void delete(Long id) throws Exception {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			/*
			 * For demo purposes, I am using Generic Exception. In a real scenario you must
			 * implement custom exception handling.
			 */ throw new Exception("Id Not Found");
		}
	}
	
//	@Post("/action")
//	public AuditLog initializeAction(@Body Map<String, Object> requestBody) {
//		List<String>[] lineItem = (List<String>[]) requestBody.get("lineItem");
//		List<String>[] sourceDoc = (List<String>[]) requestBody.get("sourceDoc");
//
//	    if (lineItem != null && sourceDoc != null) {
//	        AuditLog auditLog = new AuditLog();
//	        auditLog.setLineItems(Arrays.asList(lineItem)); // Convert to a List
//	        auditLog.setSourceDocs(Arrays.asList(sourceDoc)); // Convert to a List
//	        auditLog.initializeAction(); // Initialize action when both lineItem and sourceDoc are set
//	        return auditLog;
//	    } else {
//	        return null; // Handle default action or other cases as needed
//	    }
//	}

}
