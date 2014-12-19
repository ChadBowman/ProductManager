package net.orthus.pm;

import java.io.File;
import java.net.URI;

public class OFile extends File {

	public OFile(String pathname) {
		super(pathname);
	}
	
	public OFile(File file){
		super(file.getPath());
	}

	public String toString(){
		return this.getName();
	}

}
