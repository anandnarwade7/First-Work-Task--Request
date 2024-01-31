package com.fin.model.logs;

import java.util.List;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.model.Sort;
import io.micronaut.data.model.Sort.Order;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.http.annotation.PathVariable;

@Repository
public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {

	List<AuditLog> findBySourceDoc(String sourceDoc);

	List<AuditLog> findAllOrderByCreatedOnAsc();

	List<AuditLog> findAllOrderByCreatedOnDesc();

	List<AuditLog> findByClientName(String clientName);

	List<AuditLog> findByClientNameAndUserRole(String clientName, String roleName);

	List<AuditLog> findByClientName(String clientName, Order sort);

	List<AuditLog> findBySourceDocAndClientName(String sourceDoc, String clientName);

	@Query("select a from AuditLog a where a.sourceDoc like:key")
	List<AuditLog> findBysourceDocContaining(String key);

	@Query("SELECT a FROM AuditLog a WHERE a.clientName= :clientName AND " + "(:searchTerm is null or "
			+ "a.lineItem like concat('%', :searchTerm, '%') or " + "a.userRole like concat('%', :searchTerm, '%') or "
			+ "a.userEmail like concat('%', :searchTerm, '%') or " + "a.userName like concat('%', :searchTerm, '%') or "
			+ "a.clientName like concat('%', :searchTerm, '%') or "
			+ "a.sourceDoc like concat('%', :searchTerm, '%') or " + "a.data like concat('%', :searchTerm, '%') or "
			+ "(DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "CAST(a.logId AS string) like concat('%', :searchTerm, '%') or "
			+ "a.action like concat('%', :searchTerm, '%')) and "
			+ "a.logId is not null and a.action is not null and a.createdOn is not null")
	List<AuditLog> searchLogs(String clientName, String searchTerm);
	
	@Query("SELECT a FROM AuditLog a WHERE a.clientName = :clientName " + "AND a.sourceDoc = :sourceDoc "
			+ "AND a.logId IS NOT NULL AND a.action IS NOT NULL AND a.createdOn IS NOT NULL "
			+ "AND (:searchTerm IS NULL OR " + "a.lineItem LIKE concat('%', :searchTerm, '%') OR "
			+ "a.userRole LIKE concat('%', :searchTerm, '%') OR " + "a.userEmail LIKE concat('%', :searchTerm, '%') OR "
			+ "a.userName LIKE concat('%', :searchTerm, '%') OR "
			+ "a.clientName LIKE concat('%', :searchTerm, '%') OR "
			+ "a.sourceDoc LIKE concat('%', :searchTerm, '%') OR " + "a.data LIKE concat('%', :searchTerm, '%') OR "
			+ "(DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%Y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%y') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%M %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%e %M') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%b %e') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%e %b') = :searchTerm OR "
			+ "DATE_FORMAT(FROM_UNIXTIME(a.createdOn / 1000), '%b %e %Y') = :searchTerm) OR "
			+ "CAST(a.logId AS string) LIKE concat('%', :searchTerm, '%') OR "
			+ "a.action LIKE concat('%', :searchTerm, '%')) " + "ORDER BY a.logId DESC")
	List<AuditLog> findByClientIdAndSourceDocOrderByLogIdDesc(String clientName, String sourceDoc, String searchTerm);

	AuditLog getAuditLogById(Long id);


}
