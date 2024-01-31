package com.fin.model.notifications;

import java.util.List;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface NotificationRepository extends CrudRepository<Notifications, Long> {

	List<Notifications> findByUserName(String userName);

	void deleteByUserName(String userName);

	Notifications findByUserNameAndId(String email, long noteId);
}
