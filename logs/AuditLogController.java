package com.fin.model.logs;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.HttpResponseException;
import org.owasp.encoder.Encode;

import com.fin.model.client.Client;
import com.fin.model.client.ClientRepository;

import io.micronaut.data.model.Sort;
import io.micronaut.data.model.Sort.Order;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@ExecuteOn(TaskExecutors.IO)
@Controller("/auditlogs")
public class AuditLogController {

	@Inject
	public AuditLogRepository repository;

	@Inject
	private ClientRepository clientRepository;

	@Inject
	private AuditLogService auditLogService;

	public AuditLogController(AuditLogRepository repos, ClientRepository clientRepository) {
		this.repository = repos;
		this.clientRepository = clientRepository;
	}

	@Get("/pagenation/{pageNumber}")
	public List<AuditLog> getAllData(@PathVariable String pageNumber) {

		List<AuditLog> findAll = (List<AuditLog>) repository.findAll();
		Collections.sort(findAll, Comparator.comparing(AuditLog::getLogId).reversed());
		int newLogId = 1;
		for (AuditLog auditLog : findAll) {
			auditLog.setLogId((long) newLogId++);
		}

		int startIndex = (Integer.parseInt(pageNumber) - 1) * 20;
		int endIndex = Math.min(startIndex + 20, findAll.size());
		findAll = new ArrayList<>(findAll.subList(startIndex, endIndex));

		return findAll;
	}

	@Get("/pagenation/{clientId}/{pageNumber}")
	public List<AuditLog> getAllDataByClient(@PathVariable String clientId, @PathVariable String pageNumber) {
		try {
			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			List<AuditLog> findAll = repository.findByClientName(clientName);
			Collections.sort(findAll, Comparator.comparing(AuditLog::getLogId).reversed());
			int startIndex = (Integer.parseInt(pageNumber) - 1) * 20;
			int endIndex = Math.min(startIndex + 20, findAll.size());
			findAll = new ArrayList<>(findAll.subList(startIndex, endIndex));
			return findAll;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/filter/{sourceDoc}")
	public List<AuditLog> filterAuditLogs(@QueryValue String sourceDoc) {
		if (sourceDoc.equals("PL")) {
			return repository.findBySourceDoc("P&L");
		} else {
			return repository.findBySourceDoc(sourceDoc);
		}
	}

	@Get("/filter/{sourceDoc}/{clientId}/{pageNumber}")
	public List<AuditLog> filterAuditLogsByclientId(@PathVariable String sourceDoc, @PathVariable String clientId,
			@PathVariable int pageNumber) {
		try {

			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			List<AuditLog> docAndClientName = new ArrayList<>();
			if (sourceDoc.equals("PL")) {
				docAndClientName = repository.findBySourceDocAndClientName("P&L", clientName);
			} else {
				docAndClientName = repository.findBySourceDocAndClientName(sourceDoc, clientName);
			}
			int startIndex = (pageNumber - 1) * 20;
			int endIndex = Math.min(startIndex + 20, docAndClientName.size());
			docAndClientName = new ArrayList<>(docAndClientName.subList(startIndex, endIndex));
			return docAndClientName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/logFilter/{sourceDoc}/{clientId}")
	public List<AuditLog> AuditLogsFilterByclientId(@PathVariable String sourceDoc, @PathVariable String clientId) {
		try {

			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			List<AuditLog> docAndClientName = new ArrayList<>();
			if (sourceDoc.equals("PL")) {
				docAndClientName = repository.findBySourceDocAndClientName("P&L", clientName);
			} else {
				docAndClientName = repository.findBySourceDocAndClientName(sourceDoc, clientName);
			}
			return docAndClientName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/{id}")
	public Optional<AuditLog> get(Long id) {
		return repository.findById(id);
	}

	@Put("/{id}")
	public AuditLog update(Long id, @Body AuditLog object) throws Exception {
		if (repository.existsById(id)) {
			object.setLogId(id);
			repository.update(object);
			return object;
		} else {
			/*
			 * For demo purposes, I am using Generic Exception. In a real scenario you must
			 * implement custom exception handling.
			 */ throw new Exception("Id Not Found");
		}
	}

	@Post
	public AuditLog add(@Body AuditLog object) {

		return repository.save(object);
	}

	@Delete("/{id}")
	public void delete(Long id) throws Exception {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			/*
			 * For demo purposes, I am using Generic Exception. In a real scenario you must
			 * implement custom exception handling.
			 */ throw new Exception("Id Not Found");
		}
	}

	@Get("/sort/{order}")
	public List<AuditLog> getAll(@PathVariable String order) {
		try {
			if ("asc".equalsIgnoreCase(order)) {
				return repository.findAllOrderByCreatedOnAsc();
			} else if ("desc".equalsIgnoreCase(order)) {
				return repository.findAllOrderByCreatedOnDesc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/sort/{clientId}/{order}/{pageNumber}")
	public List<AuditLog> getAllByClient(@PathVariable String clientId, @PathVariable String order,
			@PathVariable int pageNumber) {
		try {
			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();

			List<AuditLog> byClientName = repository.findByClientName(clientName);

			Comparator<AuditLog> comparator = (log1, log2) -> {
				if ("asc".equalsIgnoreCase(order)) {
					return log1.getLogId().compareTo(log2.getLogId());
				} else if ("desc".equalsIgnoreCase(order)) {
					return log2.getLogId().compareTo(log1.getLogId());
				} else {
					return 0; // No sorting if "order" is neither "asc" nor "desc"
				}
			};

			Collections.sort(byClientName, comparator);

			int startIndex = (pageNumber - 1) * 20;
			int endIndex = Math.min(startIndex + 20, byClientName.size());

			List<AuditLog> paginatedList = byClientName.subList(startIndex, endIndex);
			return paginatedList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/count/{clientId}")
	public int getTotalNumberOfRows(@PathVariable String clientId) {
		try {

			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			List<AuditLog> findAll = repository.findByClientName(clientName);
			return findAll.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Get("/file/{clientId}")
	public HttpResponse<byte[]> downloadLogs(@PathVariable String clientId) {
		try {
			File file = this.auditLogService.createLogFile(clientId);
			String fileName = file.getName();
			System.out.println("file name=>" + fileName);
			return HttpResponse.ok(Files.readAllBytes(file.toPath())).header("Content-type", "application/octet-stream")
					.header("Content-disposition", "attachment; filename=\"" + fileName + "\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	@Get("/search/{key}")
//	public HttpResponse<List<AuditLog>> searchBySourceDoc(@PathVariable String key){
//		List<AuditLog> result = this.auditLogService.searchLog(key);
//		return HttpResponse.ok(result);
//	}

	@Get("/search/{clientId}")
	public List<AuditLog> searchLogs(@PathVariable long clientId, @QueryValue String searchTerm) {
		try {
			Client client = clientRepository.findByClientId(clientId);
			String clientName = client.getClientName();
			if (searchTerm.equals("PL")) {
				searchTerm = "P&L";
			}
			return repository.searchLogs(clientName, searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Get("/search/desc/{sourceDoc}/{clientId}")
	public List<AuditLog> searchLogsByClientIdAndSourceDocDescending(@PathVariable long clientId,
			@PathVariable String sourceDoc, @QueryValue String searchTerm) {
		try {
			Client client = clientRepository.findByClientId(clientId);
			String clientName = client.getClientName();
			if ("PL".equals(sourceDoc)) {
				sourceDoc = "P&L";
			}
			return repository.findByClientIdAndSourceDocOrderByLogIdDesc(clientName, sourceDoc, searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
