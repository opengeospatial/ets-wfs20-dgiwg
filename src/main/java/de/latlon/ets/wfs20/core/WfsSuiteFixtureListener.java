package de.latlon.ets.wfs20.core;

import java.util.Map;

import javax.xml.namespace.QName;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import de.latlon.ets.wfs20.core.domain.WfsSuiteAttribute;

/**
 * A listener that performs various tasks before and after a test suite is run, usually
 * concerned with maintaining a shared test suite fixture. Since this listener is loaded
 * using the ServiceLoader mechanism, its methods will be called before those of other
 * suite listeners listed in the test suite definition and before any annotated
 * configuration methods.
 *
 * Attributes set on an ISuite instance are not inherited by constituent test group
 * contexts (ITestContext). However, suite attributes are still accessible from lower
 * contexts.
 *
 * @see org.testng.ISuite ISuite interface
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class WfsSuiteFixtureListener implements ISuiteListener {

	@Override
	public void onStart(ISuite suite) {
		processTestFeatureTypeParameter(suite);
	}

	@Override
	public void onFinish(ISuite suite) {
	}

	private void processTestFeatureTypeParameter(ISuite suite) {
		Map<String, String> params = suite.getXmlSuite().getParameters();
		String testFeatureTypeNamespace = params.get(TestRunArg.TESTFEATURETYPENAMESPACE.toString());
		String testFeatureTypeName = params.get(TestRunArg.TESTFEATURETYPENAME.toString());
		QName testFeatureType = null;
		if (testFeatureTypeName != null && !testFeatureTypeName.isEmpty())
			testFeatureType = new QName(testFeatureTypeNamespace, testFeatureTypeName);
		suite.setAttribute(WfsSuiteAttribute.TEST_FEATURE_TYPE.getName(), testFeatureType);
	}

}