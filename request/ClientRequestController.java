package com.fin.model.request;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.text.RandomStringGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.model.business.PointOfContact;
import com.fin.model.client.Client;
import com.fin.model.client.ClientRepository;
import com.fin.model.notifications.NotificationRepository;
import com.fin.model.notifications.Notifications;
import com.fin.model.role.Role;
import com.fin.model.user.User;
import com.fin.model.user.UserRepository;

import io.micronaut.core.annotation.Nullable;
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

	protected final CommentRepository cmtRepo;
	 
//	@Autowired
//	private final JavaMailSender javaMailSender;

	public ClientRequestController(ClientRequestRepository repository, NotificationRepository notificationRepository,
			UserRepository userRepository, ClientRepository clientRepository, CommentRepository cmtRepo) {
		this.repository = repository;
		this.notificationRepository = notificationRepository;
		this.userRepository = userRepository;
		this.clientRepository = clientRepository;
		this.cmtRepo = cmtRepo;
//		this.javaMailSender = javaMailSender;
	}

	/* Having List of All Request */
	@Get("/list")
	public List<ClientRequest> getAll() throws Exception {
		 List<ClientRequest> al = (List<ClientRequest>) repository.findAll();
		 for (ClientRequest clientRequest : al) {
			 Calendar calendar = Calendar.getInstance();
				if (calendar.getTimeInMillis() >= clientRequest.getDueDate()) {
					clientRequest.setStatus(Status.closed);

					repository.update(clientRequest);
				}
				return al;
		}
		 return al;
	}

	@Get("/{requestId}")
	public ClientRequest getRequestData(@PathVariable long requestId) {
		try {
			ClientRequest findRequestById = repository.findClientRequestByRequestId(requestId);
			Calendar calendar = Calendar.getInstance();
			if (calendar.getTimeInMillis() >= findRequestById.getDueDate()) {
				findRequestById.setStatus(Status.closed);
				ClientRequest updatedRequest = repository.update(findRequestById);
				return updatedRequest;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* having list of particular reporter */
	@Get("/reporter/{reporter_id}")
	public List<ClientRequest> getRequestsByReporterId(@PathVariable Long reporter_id) {
		List<ClientRequest> requests = repository.findByReporterId(reporter_id);
		 for (ClientRequest clientRequest : requests) {
			 Calendar calendar = Calendar.getInstance();
				if (calendar.getTimeInMillis() >= clientRequest.getDueDate()) {
					if (clientRequest.getStatus().equals("newRequest")||clientRequest.getStatus().equals("inProgress")||clientRequest.getStatus().equals("submitted")) {
						clientRequest.setStatus(Status.closed);
						repository.update(clientRequest);
					}
				}
				return requests;
		}
		return requests;
	}

	/* Having List of All Request(One by One) */
	@Get
	public List<ClientRequest> getAllData() {
		List<ClientRequest> requests = (List<ClientRequest>) repository.findAll();
		for (ClientRequest request : requests) {
			try {
				request.setStatus(Status.valueOf(request.getStatus().name()));
			} catch (IllegalArgumentException e) {
				request.setStatus(Status.newRequest); // Set a default value
			}
		}
		return requests;
	}

	@Get("/requestsByClientName/{clientName}")
	public List<ClientRequest> findRequestsByClientName(@PathVariable String clientName,@QueryValue @Nullable Status status) {
		if (status != null) {
	        return repository.findByClientClientNameContainingIgnoreCaseAndStatus(clientName, status);
	    } else {
	        return repository.findByClientClientNameContainingIgnoreCase(clientName);
	    }
	}
	
	@Get("/myrequests/{requestId}")
	public Optional<ClientRequest> get(Long requestId) {
		Optional<ClientRequest> optionalRequest = repository.findById(requestId);

		if (optionalRequest.isPresent()) {
			ClientRequest request = optionalRequest.get();
			Calendar calendar = Calendar.getInstance();
			if (calendar.getTimeInMillis() >= request.getDueDate()) {
				request.setStatus(Status.closed);

				ClientRequest updatedRequest = repository.update(request);
				// closedAgoMessage = updatedRequest.getClosedAgoMessage();
				return Optional.of(updatedRequest);
			} else {
				return Optional.of(request);
			}
			// System.out.println(closedAgoMessage);
		}

		return optionalRequest;

	}

	@Get("/getComments/{requestId}")
	public List<Comment> findByRequestId(Long requestId) {
		List<Comment> cmt = cmtRepo.findByRequestIdOrderByCreatedOnDesc(requestId);
		if (cmt.containsAll(cmt)) {
			// Retrieve comments from CommentRepository by requestId
			List<Comment> comments = cmtRepo.findByRequestIdOrderByCreatedOnDesc(requestId);
			// Check if comments is empty and return an empty list if requestId is not found
			return comments;
		} else {
			return cmt;
		}
	}

	@Get("/byClient/{clientId}")
	public List<ClientRequest> findByClientClientId(@PathVariable String clientId) {
		return repository.findByClientClientId(Long.parseLong(clientId));
	}

////////////////////////To Edit & Update the Request...../////////////////////////

	@Put("/{id}")
	public ClientRequest update(Long id, @Body ClientRequest requestBody) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);
		ClientRequest userRequest = optionalRequest.get();
		User user = userRequest.getReporter();
		
		System.out.println(optionalRequest.toString());

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
				request.setClient(request.getClient());
				request.setSelectDatabase(requestBody.getSelectDatabase());
				request.setSelectLineItem(requestBody.getSelectLineItem());
				request.setSelectEditType(requestBody.getSelectEditType());
				request.setDetails(requestBody.getDetails());
				ClientRequest clientRequest = repository.update(request);
				
				Client client = request.getClient();
				System.out.println(client);
				Client byClientId = clientRepository.findByClientId(client.getClientId());
				System.out.println(byClientId);
				Set<PointOfContact> poc = byClientId.getPointOfContact();
				for (PointOfContact pointOfContact : poc) {
					if (!user.getEmail().equals(pointOfContact.getEmail())) {
					Notifications notifications = new Notifications();
					System.out.println("size of poc =>" + pointOfContact.getEmail());

					notifications.setMessage("Request " + id + " is updated by " + request.getReporter().getName());
					notifications.setUserName(pointOfContact.getEmail());
					notificationRepository.save(notifications);
					System.out.println(notifications.toString());
				}
				System.out.println(clientRequest.toString());
				return clientRequest;
			} 
			}else {
				throw new Exception("Request ID not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

/////////////////////////////////////////////////   Raise Request   /////////////////////////////////////////////////

	@Post
	public ClientRequest add(@Body ClientRequest object) {

		try {
		
			User user = object.getReporter();
			User userById = userRepository.findUserById(user.getId());

			ClientRequest clientRequest = repository.save(object);
			Long requestId = clientRequest.getRequestId();
			Client byClientId = clientRepository.findByClientId(clientRequest.getClient().getClientId());
			/* To send notification */
			Set<PointOfContact> poc = byClientId.getPointOfContact();
			for (PointOfContact pointOfContact : poc) {
				if (!user.getEmail().equals(pointOfContact.getEmail())) {
				Notifications notifications = new Notifications();

				notifications.setMessage("Request " + requestId + " is raised by " + userById.getName());
				notifications.setUserName(pointOfContact.getEmail());
				
				notificationRepository.save(notifications);
			}
				// Send OTP email
                String otp = generateOTP();  // Implement your OTP generation logic
//                sendVerificationEmail(object.getReporter().getEmail(), userById.getName(), otp);
			return clientRequest;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
//	private void sendVerificationEmail(String toEmail, String name, String otp) {
//		System.out.println("Check point 4 :::");
////        MimeMessage message = javaMailSender.createMimeMessage();
////        MimeMessageHelper helper = new MimeMessageHelper(message);
//
//        try {
////            helper.setFrom("anand.narwade@cyperts.net"); // Replace with your email
////            helper.setTo(toEmail);
////            helper.setSubject("Welcome to Cash Flow- Verify Your Otp");
////            javaMailSender.send(message);
//
//            System.out.println("Verification email sent successfully!");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.out.println("Failed to send verification email.");
//        }
//    }

	public String generateOTP() {
		System.out.println("Check point 2 :::");
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build();
        return generator.generate(6); // Generate a 6-digit OTP
    }

////////////////////////////to CHANGE Status after reviewing request by "Admin"///////////////////////////////
//	@Put("/review/{id}/{userId}")
//	public ClientRequest reviewRequest(Long id, long userId) throws Exception {
//		Optional<ClientRequest> optionalRequest = repository.findById(id);
//
//		if (optionalRequest.isPresent()) {
//			ClientRequest request = optionalRequest.get();
//
//			// bydefault STATUS will be NEW
//			if (request.getStatus() == Status.newRequest) {
//				User findUserById = userRepository.findUserById(userId);
//				request.markInProgress();
//				repository.update(request);
//				List<User> listOfAdmin = userRepository.findByRoleRoleNameEquals("admin");
//				for (User user : listOfAdmin) {
//					Notifications notifications = new Notifications();
//					String clientName = request.getClient().getClientName();
//					notifications.setMessage("Request " + id + " is escalated to " + user.getName() + "(PWC Admin) by "
//							+ findUserById.getName() + " (Client Admin) for client " + clientName);
//					notifications.setUserName(user.getEmail());
//					notificationRepository.save(notifications);
//				}
//
//				return request;
//			} else {
//				throw new Exception("Request is not in the 'New' status and cannot be reviewed.");
//			}
//		} else {
//			throw new Exception("Request ID not found.");
//		}
//	}

	@Put("/review/{id}/{userId}")
	public ClientRequest reviewRequest(Long id, long userId) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);
		User findUserById = userRepository.findUserById(userId);
		Role role = findUserById.getRole();
		if (optionalRequest.isPresent()) {
			ClientRequest request = optionalRequest.get();

			// bydefault STATUS will be NEW
			if (request.getStatus() == Status.newRequest && role.getRoleName().equals("client admin")) {
				request.markSubmitted();
				repository.update(request);
				List<User> listOfAdmin = userRepository.findByRoleRoleNameEquals("admin");
				for (User user : listOfAdmin) {
					Notifications notifications = new Notifications();
					String clientName = request.getClient().getClientName();
					notifications.setMessage("Request " + id + " is sumitted to " + user.getName() + "(PWC Admin) by "
							+ findUserById.getName() + " (Client Admin) for client " + clientName);
					notifications.setUserName(user.getEmail());
					notificationRepository.save(notifications);
				}

				return request;
			} else if (request.getStatus() == Status.submitted) {
				request.markInProgress();
				repository.update(request);

				List<User> listOfAdmin = userRepository.findByRoleRoleNameEquals("admin");
				for (User user : listOfAdmin) {
					Notifications notifications = new Notifications();
					notifications
							.setMessage("Request " + id + " is in inprogess by " + user.getName() + "(PWC Admin) ");
					notifications.setUserName(user.getEmail());
					notificationRepository.save(notifications);
				}

				return request;
			}

			else if (request.getStatus() == Status.newRequest && role.getRoleName().equals("user")) {
				request.markDeleted();
				repository.update(request);
				System.out.println(request.getStatus());
				Client client = request.getClient();
				Client byClientId = clientRepository.findByClientId(client.getClientId());
				System.out.println(byClientId);
				Set<PointOfContact> poc = byClientId.getPointOfContact();
				for (PointOfContact pointOfContact : poc) {
					if (!findUserById.getEmail().equals(pointOfContact.getEmail())) {
					Notifications notifications = new Notifications();
					System.out.println("size of poc =>" + pointOfContact.getEmail());

					notifications.setMessage("Request " + id + " is deleted by " + request.getReporter().getName());
					notifications.setUserName(pointOfContact.getEmail());
					notificationRepository.save(notifications);
					System.out.println(notifications);
				}
			}
				return request;
			}

			else {
				throw new Exception("Request is not in the 'New' status and cannot be reviewed.");
			}
		} else {
			throw new Exception("Request ID not found.");
		}
	}

////////////////////////to change Status based on "RESPONSE"//////////////////////////
	@Put("/reviewRequest/{id}/{userId}/{response}")
	public ClientRequest reviewRequest11(@PathVariable Long id, @PathVariable String response,
			@PathVariable Long userId) throws Exception {
		Optional<ClientRequest> optionalRequest = repository.findById(id);

		if (optionalRequest.isPresent()) {
			ClientRequest request = optionalRequest.get();
			Client client = request.getClient();

			if (!response.isEmpty()) {

				if ("yes".equalsIgnoreCase(response)) {
					request.markCompleted();
					List<User> listOfUsersByClient = userRepository.getByClientId(String.valueOf(client.getClientId()));
					for (User users : listOfUsersByClient) {
						Notifications notifications = new Notifications();
						User user = userRepository.findUserById(userId);
						notifications
								.setMessage("Request " + id + " is completed  by " + user.getName() + "(Pwc Admin).");
						notifications.setUserName(users.getEmail());
						notificationRepository.save(notifications);
					}

					repository.update(request);
				} else if ("no".equalsIgnoreCase(response)) {
					request.markRejected();
					User user = userRepository.findUserById(userId);
					List<User> listOfUsersByClient = userRepository.getByClientId(String.valueOf(client.getClientId()));

					System.out.println("before remove " + listOfUsersByClient.size());

					for (User users : listOfUsersByClient) {

						if (!users.getEmail().equals(user.getEmail())) {
							Notifications notifications = new Notifications();
							notifications.setMessage(
									"Request " + id + " is rejected  by " + user.getName() + "(Client Admin).");
							notifications.setUserName(users.getEmail());
							notificationRepository.save(notifications);
						}
					}

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

	@Put("addComment/{id}/{userId}")
	public Optional<ClientRequest> insertComment(@PathVariable Long id, @PathVariable Long userId,
			@Body String requestBody) {
		Optional<ClientRequest> optionalRequest = repository.findById(id);
		try {
			if (optionalRequest.isPresent()) {
				ClientRequest request = optionalRequest.get();
				Date d = new Date();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(requestBody);

				Comment cmt = new Comment();
				cmt.setRequestId(id);
				cmt.setComment(node.get("comment").asText());
				cmt.setUserId(request.getReporter().getId());
				cmt.setClientId(request.getClient().getClientId());
				cmt.setCreatedOn(d.getTime());
				cmtRepo.save(cmt);
				long originalCreatedOn = request.getCreatedOn();
				long originalDueDate = request.getDueDate();
				request.setEditedOn(d.getTime());
				request.setCreatedOn(originalCreatedOn);
				request.setDueDate(originalDueDate);

				/* To send notification */
//				to send notfication to pocs who is adding comment
				User user = userRepository.findUserById(userId);
				Role role = user.getRole();
				String roleName = role.getRoleName();
				Client client = request.getClient();
				Client byClientId = clientRepository.findByClientId(client.getClientId());
//					notifications sents to poc
				Set<PointOfContact> pointOfContact = byClientId.getPointOfContact();
				for (PointOfContact poc : pointOfContact) {
					if (!user.getEmail().equals(poc.getEmail())) {
						Notifications notifications = new Notifications();
						notifications.setMessage(
								user.getName() + " " + "(" + roleName + ")" + " commented on request id " + id);
						notifications.setUserName(poc.getEmail());
						notificationRepository.save(notifications);
					}
				}
//				notification to sent to user
				User reporterUser = request.getReporter();
				Notifications notifications = new Notifications();
				notifications
						.setMessage(user.getName() + " " + "(" + roleName + ")" + " commented on request id " + id);
				notifications.setUserName(reporterUser.getEmail());
				notificationRepository.save(notifications);

//				notification sent to admin
				List<User> listOfAdmin = userRepository.findByRoleRoleNameEquals("Admin");
				for (User adminUsers : listOfAdmin) {
					Notifications adminNotification = new Notifications();
					adminNotification
							.setMessage(user.getName() + " " + "(" + roleName + ")" + " commented on request id " + id);
					adminNotification.setUserName(adminUsers.getEmail());
					notificationRepository.save(adminNotification);
				}

				repository.update(request);
				System.out.println("7   " + request);
				return Optional.ofNullable(request);
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
	public long delete(Long id) throws Exception {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			throw new Exception("Id Not Found");
		}
		return id;
	}

//	search by client
	@Get("/search/{clientId}")
	public List<ClientRequest> searchClientRequestsbyClientId(@PathVariable long clientId,
			@QueryValue String searchTerm) {
		if (searchTerm.equals("In Progress")) {
			searchTerm = "inProgress";
		}
		return repository.searchClientRequestsByClientId(clientId, searchTerm);
	}

//	search by admin
	@Get("/searchAll")
	public List<ClientRequest> searchClientRequests(@QueryValue String searchTerm) {
		if (searchTerm.equals("In Progress")) {
			searchTerm = "inProgress";
		}
		return repository.searchClientRequests(searchTerm);
	}

	@Get("/searchByStatusForPWCAdmin/{status}")
	public List<ClientRequest> searchByStatusforPWC(@PathVariable Status status, @QueryValue String searchTerm) {
		if (searchTerm.equals("In Progress")) {
			searchTerm = "inProgress";
		}

		return repository.searchByStatusForPWC(status, searchTerm);
	}

//search by status for client Admin
	@Get("/searchByStatusForClientAdmin/{status}/{clientId}")
	public List<ClientRequest> searchByStatusforClientAdmin(@PathVariable long clientId, @PathVariable Status status,
			@QueryValue String searchTerm) {
		if (searchTerm.equals("In Progress")) {
			searchTerm = "inProgress";
		}

		return repository.searchByStatusForClientAdmin(clientId, status, searchTerm);
	}
}