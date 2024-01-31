package com.fin.model.request;

import java.util.List;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ClientRequestRepository extends CrudRepository<ClientRequest, Long> {

	List<ClientRequest> findByReporterId(Long userId);

	ClientRequest findClientRequestByRequestId(Long requestId);

	List<ClientRequest> findByClientClientId(Long clientId);

	@Query("SELECT cr FROM ClientRequest cr " + "WHERE cr.client.clientId = :clientId AND ("
			+ "CAST(cr.requestId AS string) LIKE :searchTerm OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "LOWER(cr.client.clientName) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.client.clientIndustry) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.name) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.email) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.status) LIKE CONCAT('%', LOWER(:searchTerm), '%'))")
	List<ClientRequest> searchClientRequestsByClientId(Long clientId, String searchTerm);

	@Query("SELECT cr FROM ClientRequest cr " + "WHERE "
			+ "LOWER(cr.requestId) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "LOWER(cr.client.clientName) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.client.clientIndustry) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.name) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.email) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.status) LIKE CONCAT('%', LOWER(:searchTerm), '%')")
	List<ClientRequest> searchClientRequests(String searchTerm);
	
	
	@Query("SELECT cr FROM ClientRequest cr WHERE (:status IS NULL OR cr.status = :status) AND ("
			+ "LOWER(cr.requestId) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "LOWER(cr.client.clientName) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.client.clientIndustry) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.email) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.name) LIKE CONCAT('%', LOWER(:searchTerm), '%'))")
	List<ClientRequest> searchByStatusForPWC(Status status, String searchTerm);

	@Query("SELECT cr FROM ClientRequest cr WHERE cr.client.clientId = :clientId AND (:status IS NULL OR cr.status = :status) AND ("
			+ "LOWER(cr.requestId) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.createdOn / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(cr.dueDate / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "LOWER(cr.client.clientName) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.client.clientIndustry) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.email) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR "
			+ "LOWER(cr.reporter.name) LIKE CONCAT('%', LOWER(:searchTerm), '%'))")
	List<ClientRequest> searchByStatusForClientAdmin(long clientId, Status status, String searchTerm);

	List<ClientRequest> findByClientClientNameContainingIgnoreCaseAndStatus(String searchTerm, Status status);

    List<ClientRequest> findByClientClientNameContainingIgnoreCase(String searchTerm);
    
}