package com.yoursway.autoupdate.core.tests.versiondef;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.yoursway.autoupdate.core.tests.versiondef.BasicUrlVersionDefinitionLoaderTests;
import com.yoursway.autoupdate.core.tests.versiondef.UrlVersionDefinitionLoaderErrorsTests;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BasicUrlVersionDefinitionLoaderTests.class,
        UrlVersionDefinitionLoaderErrorsTests.class, VariousInformationLoadingTests.class })
public class AllVersionDefinitionLoadingTests {
    
}
