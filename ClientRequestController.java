package com.fin.model.request;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.model.client.Client;
import com.fin.model.client.ClientRepository;
import com.fin.model.notifications.NotificationRepository;
import com.fin.model.notifications.Notifications;
import com.fin.model.user.User;
import com.fin.model.user.UserRepository;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

@ExecuteOn(TaskExecutors.IO)
@Controller("/requests")
public class ClientRequestController {

	protected final ClientRequestRepository repository;
	
	protected final NotificationRepository notificationRepository;
	
	protected final UserRepository userRepository;
	
	protected final ClientRepository clientRepository;

	public ClientRequestController(ClientRequestRepository repos, NotificationRepository notificationRepository, UserRepository userRepository, ClientRepository clientRepository) {
		this.repository = repos;
		this.notificationRepository = notificationRepository;
		this.userRepository = userRepository;
		this.clientRepository = clientRepository;
	}

//	@Get
//	public List<ClientRequest> getAll() {
//		return (List<ClientRequest>) repository.findAll();
//		}
//	@Put("/{id}")
//	public ClientRequest update(Long id, @Body ClientRequest object) throws Exception {
//		if (repository.existsById(id)) {
//			object.setRequestId(id);
//			repository.update(object);
//			return object;
//		} else {
//			
//			throw new Exception("Id Not Found");
//		}
//	}
//	@Post
//	public ClientRequest add(@Body ClientRequest object) {
//		return repository.save(object);
//	}
//
//	@Delete("/{id}")
//	public void delete(Long id) throws Exception {
//		if (repository.existsById(id)) {
//			repository.deleteById(id);
//		} else {
//			/*
//			 * For demo purposes, I am using Generic Exception. In a real scenario you must
//			 * implement custom exception handling.
//			 */ throw new Exception("Id Not Found");
//		}
//	}

	                          /* code by Anand */


//	/*         have list by reporter id          */
//	@Get("/user/{userId}")
//	public List<ClientRequest> getRequestsByUserId(Long userId) {
//		// You need to implement a method to retrieve requests where the reporter's ID
//		// matches userId
//		List<ClientRequest> requests = repository.findByReporterId(userId);
//
//		return requests;
//	}

	/*          Having List of All Request         */	
	@Get("/list")
	public List<ClientRequest> getAll() throws Exception {
		return (List<ClientRequest>) repository.findAll();
	}

	  /*     having  list of particular reporter    */
	@Get("/reporter/{reporter_id}")
	public List<ClientRequest> getRequestsByReporterId(@PathVariable Long reporter_id) {
		List<ClientRequest> requests = repository.findByReporterId(reporter_id);
		
		return requests;
	}
	
     /*    Having List of All Request(One by One)  */	
	@Get
	public List<ClientRequest> getAllData() {
		List<ClientRequest> requests = (List<ClientRequest>) repository.findAll();

		for (ClientRequest request : requests) {
			try {
				request.setStatus(Status.valueOf(request.getStatus().name()));
			} catch (IllegalArgumentException e) {
				request.setStatus(Status.NEW); // Set a default value
			}
		}
		return requests;
	}

///////////////////To have Specific Request if request is closed//////////////////
	@Get("/myrequests/{id}")
	@Transactional
	public Optional<ClientRequest> get(Long id) {
		Optional<ClientRequest> optionalRequest = repository.findById(id);

		if (optionalRequest.isPresent()) {
			ClientRequest request = optionalRequest.get();
			Calendar calendar = Calendar.getInstance();
			if (calendar.getTimeInMillis() >= request.getDueDate()) {
				request.setStatus(Status.CLOSED);

				ClientRequest updatedRequest = repository.save(request);
               // closedAgoMessage = updatedRequest.getClosedAgoMessage();
				return Optional.of(updatedRequest);
			}
			//System.out.println(closedAgoMessage);
		}

		return optionalRequest;

	}

////////////////////////To Edit & Update the Request...../////////////////////////

	@Put("/{id}")
	public ClientRequest update(Long id, @Body ClientRequest requestBody) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);

		try {
			if (optionalRequest.isPresent()) {
				ClientRequest request = optionalRequest.get();
				Date d = new Date();

				long originalCreatedOn = request.getCreatedOn();
				long originalDueDate = request.getDueDate();

//				Duration originalDuration = request.getDuration();
//				request.setDuration(originalDuration);

				request.setEditedOn(d.getTime());
				request.setCreatedOn(originalCreatedOn);
				request.setDueDate(originalDueDate);
				repository.update(request);
				return request;
			} else {
				throw new Exception("Request ID not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

///////////////////////////////////     Raise Request    /////////////////////////////////////////////////

	@Post
	public ClientRequest add(@Body ClientRequest object) {

		try {
			Client client = object.getClient();
			Client byClientId = clientRepository.findByClientId(client.getClientId());
			User user = object.getReporter();
			User userById = userRepository.findUserById(user.getId());
			Notifications notifications = new Notifications();
			notifications.setMessage("The request is raised by the " + userById.getName());
			notifications.setUserName(byClientId.getClientName());
			notificationRepository.save(notifications);
			ClientRequest clientRequest = repository.save(object);
			return clientRequest;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}		
////////////////////////////to CHANGE Status after reviewing request by "Admin"///////////////////////////////
	@Put("/review/{id}")
	public ClientRequest reviewRequest(Long id) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);

		if (optionalRequest.isPresent()) {
			ClientRequest request = optionalRequest.get();

//bydefault STATUS will be NEW
			if (request.getStatus() == Status.NEW) {

				request.markInProgress();
				repository.update(request);
				return request;
			} else {
				throw new Exception("Request is not in the 'New' status and cannot be reviewed.");
			}
		} else {
			throw new Exception("Request ID not found.");
		}
	}

////////////////////////to change Status based on "RESPONSE"//////////////////////////
	@Put("/reviewRequest/{id}")
	public ClientRequest reviewRequest11(Long id, @QueryValue(defaultValue = "") String response) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);

		if (optionalRequest.isPresent()) {
			ClientRequest request = optionalRequest.get();
/////////based on response
			if (!response.isEmpty()) {

				if ("yes".equalsIgnoreCase(response)) {
					request.markCompleted();
					repository.update(request);
				} else if ("no".equalsIgnoreCase(response)) {
					request.markRejected();
					repository.update(request);
				} else {
					throw new Exception("Invalid response. Use 'yes' or 'no'.");
				}
			}

			return request;
		} else {
			throw new Exception("Request ID not found.");
		}
	}

/////////////////////To add Comment/////////////////
	@Put("/addComment/{id}")
	public ClientRequest addCommentToRequest(@PathVariable Long id, @Body String requestBody) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);

		try {
			if (optionalRequest.isPresent()) {
				ClientRequest request = optionalRequest.get();
				Date d = new Date();
				
				//////////// Add Comment//////////
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(requestBody);
				String comment = node.get("comment").asText();
				System.out.println(comment);

				long originalCreatedOn = request.getCreatedOn();
				long originalDueDate = request.getDueDate();

				request.setComment(comment);
				request.setEditedOn(d.getTime());
				request.setCreatedOn(originalCreatedOn);
				request.setDueDate(originalDueDate);
				
				/* To send notification */
				Client client = request.getClient();
				Client byClientId = clientRepository.findByClientId(client.getClientId());
				User user = request.getReporter();
				User userById = userRepository.findUserById(user.getId());
				Notifications notifications = new Notifications();
				notifications.setMessage(userById.getName()+ "added comment on request");
				notifications.setUserName(byClientId.getClientName());
				notificationRepository.save(notifications);				
				
				repository.update(request);
				return request;
			} else {
				throw new Exception("Request ID not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

/////////////////////////////////// DELETE BY ID ///////////////////////////////////////////
	@Delete("/{id}")
	public String delete(Long id) throws Exception {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			throw new Exception("Id Not Found");
		}
		return "Request Deleted Successfully" + id;
	}
	
	
	
	public String SendNotify() {	
		return "";
	}
}
