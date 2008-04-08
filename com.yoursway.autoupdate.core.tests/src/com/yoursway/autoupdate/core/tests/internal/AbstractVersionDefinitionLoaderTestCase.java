package com.yoursway.autoupdate.core.tests.internal;

import static com.google.common.base.Functions.TO_STRING;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.sortedCopy;
import static com.google.common.collect.Lists.transform;
import junit.framework.Assert;

import org.junit.Before;

import com.yoursway.autoupdate.core.tests.mocks.LocalDirectoryUpdater;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.IVersionDefinitionLoader;
import com.yoursway.autoupdate.core.versions.definitions.InvalidVersionDefinitionException;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinitionNotAvailable;

public abstract class AbstractVersionDefinitionLoaderTestCase extends Assert {
    
    protected IVersionDefinitionLoader loader;
    
    @Before
    public void prepare() throws Exception {
        loader = new LocalDirectoryUpdater(getClass().getSimpleName());
    }
    
    protected String filesOf(Version version) throws VersionDefinitionNotAvailable,
            InvalidVersionDefinitionException {
        return sortedCopy(transform(newArrayList(loader.loadDefinition(version).files()), TO_STRING))
                .toString();
    }
    
}
