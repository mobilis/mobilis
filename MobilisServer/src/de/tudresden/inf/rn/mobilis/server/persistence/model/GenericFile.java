package de.tudresden.inf.rn.mobilis.server.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class GenericFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Lob
	private byte[] file;
	private String fileUserId;
	
	public byte[] getFile() {
		return file;
	}
	
	public void setFile(byte[] file) {
		this.file = file;
	}
	
	public String getFileUserId() {
		return fileUserId;
	}

	public void setFileUserId(String userId) {
		this.fileUserId = userId;
	}

	@Override
	public String toString() {
		return "File [file user id: " + fileUserId + ", file size: " + file.length + "]";
	}
}
