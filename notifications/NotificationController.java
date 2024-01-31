package com.fin.model.notifications;

import java.util.List;
import java.util.stream.Collectors;

import com.fin.model.client.Client;
import com.fin.model.client.ClientRepository;
import com.fin.model.role.Role;
import com.fin.model.user.User;
import com.fin.model.user.UserRepository;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

@Controller("/notifications")
public class NotificationController {

	protected final NotificationRepository repository;

	protected final ClientRepository clientRepository;

	protected final UserRepository userRepository;

	public NotificationController(NotificationRepository repository, ClientRepository clientRepository,
			UserRepository userRepository) {
		this.repository = repository;
		this.clientRepository = clientRepository;
		this.userRepository = userRepository;
	}

	// to get notification for admin
	@Get
	public List<Notifications> getAllNotifications() {
		List<Notifications> notificationList = (List<Notifications>) repository.findAll();
		for (Notifications notification : notificationList) {
			notification.setSeen(true);
			repository.update(notification);
		}

		return notificationList;
	}

//	to clear all notification (admin)
	@Delete
	public void deleteAllNotes() {
		repository.deleteAll();

	}

//	clear all notification by client
	@Delete("/deleteNotes/{clientId}")
	public void deleteAllNotsByClient(@PathVariable long clientId) {
		Client byClientId = clientRepository.findByClientId(clientId);
		String clientName = byClientId.getClientName();
		List<Notifications> notificationList = repository.findByUserName(clientName);
		for (Notifications notifications : notificationList) {
			long id = notifications.getId();
			repository.deleteById(id);
		}

	}

	@Get("/{userId}")
	public List<Notifications> getAllNotification(@PathVariable long userId) {
		try {

			User findUserById = userRepository.findUserById(userId);
			List<Notifications> notificationList = repository.findByUserName(findUserById.getEmail());
			for (Notifications notification : notificationList) {
				notification.setSeen(true);
				repository.update(notification);
			}

			return notificationList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/notifyUser/{reporterId}")
	public List<Notifications> getAllNotificationByUser(@PathVariable String reporterId) {
		try {
			User byUserId = userRepository.findUserById(Long.parseLong(reporterId));
			String userName = byUserId.getName();
			List<Notifications> notificationList = repository.findByUserName(userName);
			for (Notifications notification : notificationList) {
				notification.setSeen(true);
				repository.update(notification);
			}
			return notificationList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/count/{clientId}")
	public int getNotificationCount(@PathVariable String clientId) {
		try {
			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			List<Notifications> list = repository.findByUserName(clientName);
			List<Notifications> filteredList = list.stream().filter(notification -> notification.isSeen() == false)
					.collect(Collectors.toList());
			int count = filteredList.size();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Get("/unseenCount/{userId}")
	public int getunseenNoteCount(@PathVariable long userId) {
		try {
			User findUserById = userRepository.findUserById(userId);
			List<Notifications> notificationList = repository.findByUserName(findUserById.getEmail());
			List<Notifications> filteredList = notificationList.stream().filter(notification -> notification.isSeen() == false)
					.collect(Collectors.toList());
			int count = filteredList.size();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Delete("/user/{clientId}")
	public String clearAllNotification(@PathVariable String clientId) {
		try {
			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			repository.deleteByUserName(clientName);
			return "All notification has been cleared.";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Delete("/clearNotification/{userId}")
	public String clearAllNotesByUser(@PathVariable long userId) {
		try {
			User findUserById = userRepository.findUserById(userId);
			repository.deleteByUserName(findUserById.getEmail());
			return "Notification deleted";
//			List<Notifications> findByUserName = repository.findByUserName(findUserById.getEmail());
//			for (Notifications notifications : findByUserName) {
//				if (repository.existsById(notifications.getId())) {
//					repository.deleteById(notifications.getId());
//					return "Notification deleted";
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Delete("/deleteNotesById/{noteId}")
	public long clearNotesByUser(@PathVariable long noteId) {
		try {
			if (repository.existsById(noteId)) {
				repository.deleteById(noteId);
				return noteId;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Delete("/{id}")
	public long clearNotification(@PathVariable long id) {
		try {
			if (repository.existsById(id)) {
				repository.deleteById(id);
				return id;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
