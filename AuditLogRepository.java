package com.fin.model.logs;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;


@Repository
public interface AuditLogRepository extends CrudRepository<AuditLog,Long>{

}