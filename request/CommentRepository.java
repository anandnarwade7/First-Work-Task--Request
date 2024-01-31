package com.fin.model.request;

import java.util.List;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface CommentRepository extends CrudRepository<Comment , Long>{

//	List<Comment> findByRequestId(long requestId);
    List<Comment> findByRequestIdOrderByCreatedOnDesc(long requestId);


}
