package com.yoursway.autoupdate.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.yoursway.autoupdate.core.tests.versiondef.AllVersionDefinitionLoadingTests;

@RunWith(Suite.class)
@Suite.SuiteClasses( { IntegrationTests.class, SemiIntegrationTests.class,
        AllVersionDefinitionLoadingTests.class, UpdatePlanBuilderTests.class })
public class AllTests {
    
}
