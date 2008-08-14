package xxx.library;

import static com.google.common.collect.Maps.newHashMap;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class LibraryState {
    
    private final Map<URL, FileState> states = newHashMap();
    
    LibraryState(Collection<FileState> states) {
        for (FileState state : states)
            this.states.put(state.url, state);
    }
    
    public FileState stateOf(URL url) {
        return states.get(url);
    }
}
