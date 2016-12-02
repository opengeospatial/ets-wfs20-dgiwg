package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.latlon.ets.core.keyword.DfddKeywordMatcher;
import de.latlon.ets.core.keyword.DfddKeywordMatcherFromFile;
import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the service contains at least one expected keywords.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesKeywordTest extends WfsBaseFixture {

    private static final DfddKeywordMatcher DFDD_KEYWORD_MATCHER = new DfddKeywordMatcherFromFile();

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.18, Requirement 3")
    public void wfsCapabilitiesContainsKeywordFromDFDDRegister()
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        List<String> keywords = parseKeywords( wfsMetadata );
        boolean atLeastOneKeywordIsFromDfdd = DFDD_KEYWORD_MATCHER.containsAtLeastOneDfddKeyword( keywords );

        assertTrue( atLeastOneKeywordIsFromDfdd,
                    "Invalid keywords, expected is at least one keyword from DFDD, but is " + keywords );
    }

    private List<String> parseKeywords( Document wmsCapabilities )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathExpr = "//wfs:WFS_Capabilities/ows:ServiceIdentification/ows:Keywords/ows:Keyword";
        NodeList keywordNodes = (NodeList) createXPath().evaluate( xPathExpr, wmsCapabilities, XPathConstants.NODESET );
        List<String> keywords = new ArrayList<String>();
        for ( int keywordNodeIndex = 0; keywordNodeIndex < keywordNodes.getLength(); keywordNodeIndex++ ) {
            Node keywordNode = keywordNodes.item( keywordNodeIndex );
            String keyword = keywordNode.getTextContent();
            if ( keyword != null )
                keywords.add( keyword.trim() );
        }
        return keywords;
    }

}