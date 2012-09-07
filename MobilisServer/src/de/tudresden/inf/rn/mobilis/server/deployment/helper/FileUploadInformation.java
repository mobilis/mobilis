package de.tudresden.inf.rn.mobilis.server.deployment.helper;

/**
 * This class stores information about an upcoming file upload.
 * 
 * @author ubuntudroid
 *
 */
public class FileUploadInformation {
	public String fileName = "";
	public boolean autoDeploy = false;
	public boolean singleMode = false;
	
	public FileUploadInformation(String fileName) {
		this.fileName = fileName;
	}
	
	public FileUploadInformation(String fileName, boolean autoDeploy, boolean singleMode) {
		this.fileName = fileName;
		this.autoDeploy = autoDeploy;
		this.singleMode = singleMode;
	}
}
