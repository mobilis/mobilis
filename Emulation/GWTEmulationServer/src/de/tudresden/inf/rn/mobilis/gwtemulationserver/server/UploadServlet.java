package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// Parse the request
		List items = null;
		try {
			items = upload.parseRequest(req);
		} catch (FileUploadException e) {
			System.err.println("Fehler beim Parsen des Requests: " + e.getMessage());
		}
		
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = (FileItem) iter.next();
		    if (item.isFormField()) {
		        System.out.println("isFormField: " + item.getFieldName() + " :: " + item.getString());
		    } else {
		    	System.out.println("processFile");
		        processFile(item);
		    }
		}
		
	}

	private void processFile(FileItem item) {
		
		String scriptPath = getServletContext().getRealPath("skripte");
		File dir = new File(scriptPath);
		if(!dir.exists()) {
			System.out.println("Script-Dir don't exist, creating dir: " + scriptPath);
			dir.mkdir();
		}
		String scriptFilePath = scriptPath + File.separator + item.getName();
		System.out.println("Upload Marker 1");
		File newFile = new File(scriptFilePath);
		System.out.println("Upload Marker 2");
		if(newFile.exists()) {
			System.out.println("Upload Marker 3");
			Integer count = 0;
			while(newFile.exists()) {
				System.out.println("Upload Marker 4");
				String fileName = scriptFilePath.substring(0, scriptFilePath.length()-4) + "_" + count.toString() + ".xml";
				newFile = new File(fileName);
				count++;
			}
		} else {
			System.out.println("scriptPath: existiert nicht");
		}
		try {
			System.out.println("writeFile");
			item.write(newFile);
		} catch (Exception e) {
			System.err.println("Fehler beim Speichern des Skriptes: " + e.getMessage());
		}
		
	}

}
