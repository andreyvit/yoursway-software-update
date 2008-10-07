package com.yoursway.autoupdater.core.auxiliary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

class DefinitionReader {
    
    private final BufferedReader reader;
    private final List<String[]> preReadLines = new LinkedList<String[]>();
    
    public DefinitionReader(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
    }
    
    public String[] readLine() throws IOException {
    	if (!preReadLines.isEmpty()) {
    		return preReadLines.get(0);
    	}
        return nextLine();
    }

	private String[] nextLine() throws IOException {
		while (true) {
            String line = reader.readLine();
            if (line == null)
                return null;
            
            if (line.length() == 0)
                continue;
            String[] fields = line.split("\t");
            if (fields.length == 0)
                continue;
            
            return fields;
        }
	}
    
    public String[] lookAhead() throws IOException {
    	String [] line = nextLine();
    	if (null != line || !preReadLines.contains(null)) {  //XXX		
    		preReadLines.add(line);
    	}
    	return line;
    }
    
}
