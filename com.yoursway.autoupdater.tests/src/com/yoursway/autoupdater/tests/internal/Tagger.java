package com.yoursway.autoupdater.tests.internal;

import java.io.File;

public interface Tagger {
    
    String[] tagsFor(File file);
    
}
