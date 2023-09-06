package com.fin.model.request;


import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;


@Repository
public interface ClientRequestRepository extends CrudRepository<ClientRequest,Long>{

}