package com.yoursway.autoupdate.launching;

import org.eclipse.core.runtime.Platform;

import com.yoursway.autoupdate.internal.launching.IVMRunner;
import com.yoursway.autoupdate.internal.launching.MacOSXJavaVM;
import com.yoursway.autoupdate.internal.launching.MacOSXVMRunner;
import com.yoursway.autoupdate.internal.launching.StandardJavaVM;
import com.yoursway.autoupdate.internal.launching.StandardVMRunner;

public class VmRunners {

    public static IVMRunner createRunner() {
    	if (Platform.getOS().equals(Platform.OS_MACOSX))
    		return new MacOSXVMRunner(new MacOSXJavaVM());
    	return new StandardVMRunner(new StandardJavaVM());
    
    }
    
}
