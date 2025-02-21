package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Node;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the capabilities defines constraints on operation.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesOperationConstraintsTest extends WfsBaseFixture {

	private static final String XPATH_SERVICE_CONSTRAINT = "//wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Constraint[@name='%s']/ows:DefaultValue";

	private static final String XPATH_OPERATION = "//wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation[@name='%s']";

	private static final String XPATH_CONSTRAINT_PER_OPERATION = "ows:Constraint[@name='%s']/ows:DefaultValue";

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.3., S.31, Requirement 22")
	public void wfsCapabilitiesAutomaticDataLockingExists()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		String constraintName = "AutomaticDataLocking";
		String xPathServiceConstraint = String.format(XPATH_SERVICE_CONSTRAINT, constraintName);
		Node serviceConstraintNode = selectNode(xPathServiceConstraint);
		boolean hasServiceOrOperationConstraint = serviceConstraintNode != null;
		if (serviceConstraintNode == null) {
			Map<String, Node> operationConstraintNodes = retrieveOperationToConstraint(constraintName, "Transaction");
			hasServiceOrOperationConstraint = operationConstraintNodes.get("Transaction") != null;
		}
		assertTrue(hasServiceOrOperationConstraint,
				"Constraint 'AutomaticDataLocking' is missing. Expected is the constraint as service or "
						+ "operation constraints for operation 'Transaction'");
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.3., S.31, Requirement 22")
	public void wfsCapabilitiesCountDefaultGreaterOrEqualTo0()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		String constraintName = "CountDefault";
		String xPathServiceConstraint = String.format(XPATH_SERVICE_CONSTRAINT, constraintName);
		Node serviceConstraintNode = selectNode(xPathServiceConstraint);
		boolean hasServiceConstraint = serviceConstraintNode != null;

		Map<String, Node> operationConstraintNodes = retrieveOperationToConstraintForGetFeatureBundle(constraintName);
		boolean hasOperationConstraints = hasConstraintForAllOperations(operationConstraintNodes);

		assertTrue(hasServiceConstraint || hasOperationConstraints,
				"Constraint 'CountDefault' is missing. Expected is the constraint as service or "
						+ "operation constraints for operations 'GetFeature', 'GetFeatureWithLock' "
						+ "and 'GetPropetryValue'");

		if (hasServiceConstraint) {
			int countDefault = asInteger(serviceConstraintNode);
			assertTrue(countDefault >= 0, "CountDefault must be >= 0 but is " + countDefault);
		}
		if (hasOperationConstraints) {
			for (Entry<String, Node> operationToConstraintEntry : operationConstraintNodes.entrySet()) {
				int countDefault = asInteger(operationToConstraintEntry.getValue());
				assertTrue(countDefault >= 0, "CountDefault for operation constraint "
						+ operationToConstraintEntry.getKey() + " must be >= 0 but is " + countDefault);
			}
		}
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.3., S.31, Requirement 22")
	public void wfsCapabilitiesResolveTimeoutDefaultIs300()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		String constraintName = "ResolveTimeoutDefault";
		String xPathServiceConstraint = String.format(XPATH_SERVICE_CONSTRAINT, constraintName);
		Node serviceConstraintNode = selectNode(xPathServiceConstraint);
		boolean hasServiceConstraint = serviceConstraintNode != null;

		Map<String, Node> operationConstraintNodes = retrieveOperationToConstraintForGetFeatureBundle(constraintName);
		boolean hasOperationConstraints = hasConstraintForAllOperations(operationConstraintNodes);

		assertTrue(hasServiceConstraint || hasOperationConstraints,
				"Constraint 'ResolveTimeoutDefault' is missing. Expected is the constraint as service or "
						+ "operation constraints for operations 'GetFeature', 'GetFeatureWithLock' "
						+ "and 'GetPropetryValue'");

		if (hasServiceConstraint) {
			int resolveTimeOutDefault = asInteger(serviceConstraintNode);
			assertTrue(resolveTimeOutDefault == 300,
					"ResolveTimeoutDefault must be 300 but is " + resolveTimeOutDefault);
		}
		if (hasOperationConstraints) {
			for (Entry<String, Node> operationToConstraintEntry : operationConstraintNodes.entrySet()) {
				int resolveTimeOutDefault = asInteger(operationToConstraintEntry.getValue());
				assertTrue(resolveTimeOutDefault == 300, "ResolveTimeoutDefault for operation constraint "
						+ operationToConstraintEntry.getKey() + " must be 300 but is " + resolveTimeOutDefault);
			}
		}
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.3., S.31, Requirement 22")
	public void wfsCapabilitiesResolveLocalScopeIsAll()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		String constraintName = "ResolveLocalScope";
		String xPathServiceConstraint = String.format(XPATH_SERVICE_CONSTRAINT, constraintName);
		Node serviceConstraintNode = selectNode(xPathServiceConstraint);
		boolean hasServiceConstraint = serviceConstraintNode != null;

		Map<String, Node> operationConstraintNodes = retrieveOperationToConstraintForGetFeatureBundle(constraintName);
		boolean hasOperationConstraints = hasConstraintForAllOperations(operationConstraintNodes);

		assertTrue(hasServiceConstraint || hasOperationConstraints,
				"Constraint 'ResolveLocalScope' is missing. Expected is the constraint as service or "
						+ "operation constraints for operations 'GetFeature', 'GetFeatureWithLock' "
						+ "and 'GetPropetryValue'");

		if (hasServiceConstraint) {
			String resolveLocalScope = asString(serviceConstraintNode);
			assertNotNull(resolveLocalScope, "ResolveLocalScope is missing");
		}
		if (hasOperationConstraints) {
			for (Entry<String, Node> operationToConstraintEntry : operationConstraintNodes.entrySet()) {
				String resolveLocalScope = asString(operationToConstraintEntry.getValue());
				assertNotNull(resolveLocalScope, "ResolveLocalScope for operation constraint "
						+ operationToConstraintEntry.getKey() + " is missing");
			}
		}
	}

	private Map<String, Node> retrieveOperationToConstraintForGetFeatureBundle(String constraintName)
			throws XPathFactoryConfigurationException, XPathExpressionException {
		return retrieveOperationToConstraint(constraintName, "GetFeature", "GetFeatureWithLock", "GetPropertyValue");
	}

	private Map<String, Node> retrieveOperationToConstraint(String constraintName, String... operationNames)
			throws XPathFactoryConfigurationException, XPathExpressionException {
		Map<String, Node> operationConstraintNode = new HashMap<String, Node>();
		XPath xPath = createXPath();
		for (String operationName : operationNames) {
			String xPathOperationExpression = String.format(XPATH_OPERATION, operationName);
			Node operationNode = (Node) xPath.evaluate(xPathOperationExpression, wfsMetadata, XPathConstants.NODE);
			Node constraintValue = retrieveConstraintNodeFromOperationNode(constraintName, xPath, operationNode);
			operationConstraintNode.put(operationName, constraintValue);
		}
		return operationConstraintNode;
	}

	private Node retrieveConstraintNodeFromOperationNode(String constraintName, XPath xPath, Node operationNode)
			throws XPathExpressionException {
		if (operationNode != null) {
			String xPathConstraintExpression = String.format(XPATH_CONSTRAINT_PER_OPERATION, constraintName);
			return (Node) xPath.evaluate(xPathConstraintExpression, operationNode, XPathConstants.NODE);
		}
		return null;
	}

	private boolean hasConstraintForAllOperations(Map<String, Node> operationConstraintToNodes) {
		for (Entry<String, Node> operationConstraintToNode : operationConstraintToNodes.entrySet()) {
			if (operationConstraintToNode.getValue() == null)
				return false;
		}
		return true;
	}

	private Node selectNode(String xPathXml) throws XPathFactoryConfigurationException, XPathExpressionException {
		XPath xPath = createXPath();
		return (Node) xPath.evaluate(xPathXml, wfsMetadata, XPathConstants.NODE);
	}

	private int asInteger(Node xPathXml) throws XPathFactoryConfigurationException, XPathExpressionException {
		String value = asString(xPathXml);
		return Integer.parseInt(value);
	}

	private String asString(Node serviceConstraintNode) {
		String textContent = serviceConstraintNode.getTextContent();
		if (textContent == null || textContent.isEmpty())
			return null;
		return textContent;
	}

}