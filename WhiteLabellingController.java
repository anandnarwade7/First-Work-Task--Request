package com.fin.model.whitelablelling;

import java.io.IOException;
import java.util.List;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
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
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public WhiteLabelling uploadFile(@Body WhiteLabelling whiteLabelling, CompletedFileUpload file) throws IOException {
		whiteLabelling.setClientId(whiteLabelling.getClientId());
		whiteLabelling.setColourCode(whiteLabelling.getColourCode());
		whiteLabelling.setFileName(file.getFilename());
		whiteLabelling.setFileType(file.getContentType().toString());
		whiteLabelling.setFileData(file.getBytes());
		return repository.save(whiteLabelling);
	}

	@Get("/download/{clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	public WhiteLabelling downloadFile(String clientId) throws Exception {
		try {
			WhiteLabelling findByClientId = repository.findByClientId(clientId);
			return findByClientId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Delete("/{clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteData(String clientId) {
		try {
			WhiteLabelling findByClientId = repository.findByClientId(clientId);
			if(findByClientId !=null) {
			repository.deleteByClientId(clientId);
			return "WhiteLebelling data deleted for client "+clientId;
			}else {
				throw new Exception("Id not found");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

//	@Put("/{id}")
//	public WhiteLabelling update(Long id, @Body WhiteLabelling object) throws Exception {
//		if (repository.existsById(id)) {
//			object.setId(id);
//			repository.update(object);
//			return object;
//		} else {
//
//			throw new Exception("Id Not Found");
//		}
//	}

//	@Get("/{id}")
//	public Optional<WhiteLabelling> get(Long id) {
//		return repository.findById(id);
//	}

//	@Get("/download/{id}")
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//    public HttpResponse<byte[]> downloadFile(Long id) throws Exception {
//        Optional<WhiteLabelling> whiteLabellingOptional = repository.findById(id);
//        if (whiteLabellingOptional.isPresent()) {
//            WhiteLabelling whiteLabelling = whiteLabellingOptional.get();
//
//            byte[] fileData = whiteLabelling.getFileData();
//            String fileName = whiteLabelling.getFileName();
//            String fileType = whiteLabelling.getFileType();
//
//            HttpHeaders headers = HttpHeaders
//                .contentType(MediaType.parseMediaType(fileType))
//                .contentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
//
//            return HttpResponse
//                .ok(fileData)
//                .headers(headers);
//        } else {
//            throw new NotFoundException("File not found");
//        }
//    }