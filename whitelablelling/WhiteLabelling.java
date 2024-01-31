package com.fin.model.whitelablelling;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "whiteLabelling")
public class WhiteLabelling implements Serializable {

	private static final long serialVersionUID = 12341439120L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String clientId;

	private String fileTag;

	private String fileName;

	private String fileType;
	@Lob
//	@Column(name = "file_data", columnDefinition = "BLOB")
	private byte[] fileData;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getFileTag() {
		return fileTag;
	}

	public void setFileTag(String fileTag) {
		this.fileTag = fileTag;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	@Override
	public String toString() {
		return "WhiteLabelling [id=" + id + ", clientId=" + clientId + ", fileTag=" + fileTag + ", fileName=" + fileName
				+ ", fileType=" + fileType + ", fileData=" + Arrays.toString(fileData) + "]";
	}

}
