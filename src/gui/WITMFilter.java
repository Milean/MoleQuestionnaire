package gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class WITMFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(".witm");
	}

	@Override
	public String getDescription() {
		return ".witm files";
	}
	
}