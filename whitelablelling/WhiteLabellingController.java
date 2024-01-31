package com.fin.model.whitelablelling;

import java.io.IOException;
import java.util.List;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

import io.micronaut.http.multipart.CompletedFileUpload;

@Controller("/whiteLabelling")
public class WhiteLabellingController {

	protected final WhiteLabellingRepository repository;

	public WhiteLabellingController(WhiteLabellingRepository repos) {
		this.repository = repos;
	}

	@Get
	public List<WhiteLabelling> getAll() {
		return (List<WhiteLabelling>) repository.findAll();
	}

	@Post("/upload")
	@Consumes(value = { MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA })
	public WhiteLabelling uploadFile(@Body WhiteLabelling whiteLabelling, CompletedFileUpload file) throws IOException {
		try {
			List<WhiteLabelling> findByClientIdAndFileTag = repository
					.getByClientIdAndFileTag(whiteLabelling.getClientId(), whiteLabelling.getFileTag());
			if (!findByClientIdAndFileTag.isEmpty()) {
				repository.deleteAllByClientIdAndFileTag(whiteLabelling.getClientId(), whiteLabelling.getFileTag());
			}
			whiteLabelling.setClientId(whiteLabelling.getClientId());
			whiteLabelling.setFileTag(whiteLabelling.getFileTag());
			whiteLabelling.setFileName(file.getFilename());
			whiteLabelling.setFileType(file.getContentType().toString());
			whiteLabelling.setFileData(file.getBytes());

			return repository.save(whiteLabelling);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}


	@Get("/getByFileTag/{fileTag}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WhiteLabelling> downloadFile(@PathVariable String fileTag) {
		return repository.findByFileTag(fileTag);
	}

	@Get("/getByClientId/{clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WhiteLabelling> downloadFileByClientId(@PathVariable String clientId) {
		List<WhiteLabelling> findByClientId = repository.findByClientId(clientId);
		return findByClientId;
	}

	@Get("/getByClientId/{clientId}/{fileTag}	")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WhiteLabelling> downloadFileByClientIdAndTag(@PathVariable String clientId,
			@PathVariable String fileTag) {
		List<WhiteLabelling> findByClientIdAndTag = repository.findByClientIdAndFileTag(clientId, fileTag);
		return findByClientIdAndTag;
	}

	@Delete("/deleteByTag/{fileTag}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteAllFlesByFileTag(@PathVariable String fileTag) {
		try {
			List<WhiteLabelling> foundItems = repository.findByFileTag(fileTag);
			if (!foundItems.isEmpty()) {
				repository.deleteByFileTag(fileTag);
				return "WhiteLebelling data deleted for fileTag " + fileTag;
			} else {
				throw new Exception("File tag not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Delete("/deleteByClientId/{clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFilesByClientId(@PathVariable String clientId) {
		try {
			List<WhiteLabelling> foundItems = repository.findByClientId(clientId);
			if (!foundItems.isEmpty()) {
				repository.deleteByClientId(clientId);
				return "WhiteLabelling data deleted for clientId " + clientId;
			} else {
				throw new Exception("Data for clientId not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Delete("/deleteByClientIdAndFileTag/{clientId}/{fileTag}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFileByClientIdAndFileTag(@PathVariable String clientId, @PathVariable String fileTag) {
		try {
			List<WhiteLabelling> foundItems = repository.findByClientIdAndFileTag(clientId, fileTag);
			if (!foundItems.isEmpty()) {
				repository.deleteByClientIdAndFileTag(clientId, fileTag);
				return "WhiteLebelling data deleted for clientId " + clientId + " and fileTag " + fileTag;
			} else {
				throw new Exception("File with clientId and fileTag not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Delete("/{id}")
	public void delete(Long id) throws Exception {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			throw new Exception("Id Not Found");
		}

	}
}