package com.yoursway.autoupdate.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BasicUrlVersionDefinitionLoaderTests.class,
		UrlVersionDefinitionLoaderErrorsTests.class,
		UpdatePlanBuilderTests.class })
public class AllTests {

}
