package net.orthus.pm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public class FTPManager {
	
	
	public boolean upLoad(String path, File upload){
		if(upload.isDirectory())
			return up(path, upload.listFiles());
		else
			return up(path, upload);
	}

	
	private boolean up(String path, File[] upload){
		
		FTPClient ftp = new FTPClient();
		
			try {
				ftp.connect("ftp.orthus.net");
				ftp.login("ebay@orthus.net", "ebay123");
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
	
				for(int i=0; i<upload.length; i++){
					ftp.storeFile(path + upload[i].getName(), 
							new FileInputStream(upload[i]));
					System.out.println("Uploaded: " + path + upload[i].getName());
				}
				
				ftp.logout();
				ftp.disconnect();
				return true;
				
			} catch (IOException e) {
				System.err.println("Upload failed!");
				e.printStackTrace();
				return false;
			}
			
		
	}
	
	private boolean up(String path, File toUpload){
		
		FTPClient ftp = new FTPClient();
		
		try {
			ftp.connect("ftp.orthus.net");
			ftp.login("ebay@orthus.net", "ebay123");
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			ftp.storeFile(path, new FileInputStream(toUpload));
			System.out.println("Uploaded: " + path + toUpload.getName());
			
			ftp.logout();
			ftp.disconnect();
			return true;
			
		} catch (IOException e) {
			System.err.println("Upload for " + toUpload.getName() + " Failed!");
			e.printStackTrace();
			return false;
		}		
	}
}
