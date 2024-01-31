package com.fin.model.request;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/comment")
public class CommentController {
	
	@Inject
	private CommentRepository commentRepository;
	
	
	@Post
	public Comment addComment(@Body Comment comment) {
		comment.setCreatedOn(System.currentTimeMillis());
		Comment newcomment = this.commentRepository.save(comment);
		return newcomment;
	}

}
