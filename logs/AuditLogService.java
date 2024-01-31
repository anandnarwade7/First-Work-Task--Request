package com.fin.model.logs;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fin.model.client.Client;
import com.fin.model.client.ClientRepository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AuditLogService {

	@Inject
	public AuditLogRepository repository;

	@Inject
	private ClientRepository clientRepository;

	public File createLogFile(String clientId) {
		try {
			Client client = clientRepository.findByClientId(Long.parseLong(clientId));
			String clientName = client.getClientName();
			List<AuditLog> findAll = repository.findByClientName(clientName);
			Collections.sort(findAll, Comparator.comparing(AuditLog::getLogId).reversed());
			File ff = File.createTempFile(clientName, ".xlsx");

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet(clientName + " Audit");
			int rowindex = 0;
			int idx = 1;
			Row rowHeading = sheet.createRow(rowindex++);
			
			rowHeading.createCell(0).setCellValue("AuditId");
			rowHeading.createCell(1).setCellValue("Line Items");
			rowHeading.createCell(2).setCellValue("User Role");
			rowHeading.createCell(3).setCellValue("User Name");
			rowHeading.createCell(4).setCellValue("User Email");
			rowHeading.createCell(5).setCellValue("Source Document");
			rowHeading.createCell(6).setCellValue("Discription");
			rowHeading.createCell(7).setCellValue("Time");

			for (AuditLog logs : findAll) {
				Instant instant = Instant.ofEpochMilli(logs.getCreatedOn());
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, hh:mm:ss a");
				String formattedDateTime = formatter.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
				Row row = sheet.createRow(rowindex++);
				row.createCell(0).setCellValue(idx++);
				row.createCell(1).setCellValue(logs.getLineItem());
				row.createCell(2).setCellValue(logs.getUserRole());
				row.createCell(3).setCellValue(logs.getUserName());
				row.createCell(4).setCellValue(logs.getUserEmail());
				row.createCell(5).setCellValue(logs.getSourceDoc());
				row.createCell(6).setCellValue(logs.getAction());
				row.createCell(7).setCellValue(formattedDateTime);
			}
			FileOutputStream fileOutputStream = new FileOutputStream(ff);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return ff;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<AuditLog> searchLog(String key) {
		List<AuditLog> list =this.repository.findBysourceDocContaining("%"+key+"%");
		return list;
	}

	

}
