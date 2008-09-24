package com.yoursway.autoupdater.core.auxiliary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class DefinitionReader {
    
    private final BufferedReader reader;
    
    public DefinitionReader(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
    }
    
    public String[] readLine() throws IOException {
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
    
}
