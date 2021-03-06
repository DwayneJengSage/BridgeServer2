package org.sagebionetworks.bridge.models.assessments;

import static org.sagebionetworks.bridge.TestUtils.mockConfigResolver;
import static org.sagebionetworks.bridge.config.Environment.LOCAL;
import static org.sagebionetworks.bridge.config.Environment.UAT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import com.fasterxml.jackson.databind.JsonNode;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.sagebionetworks.bridge.config.BridgeConfig;
import org.sagebionetworks.bridge.json.BridgeObjectMapper;
import org.sagebionetworks.bridge.models.appconfig.ConfigResolver;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AssessmentReferenceTest extends Mockito {

    @Mock
    BridgeConfig mockConfig;
    
    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void equalsVerifier() {
        // The reference may or may not have been resolved to include identifiers, the GUID
        // should be globally unique for assessement references and is sufficient for 
        // equality. This makes detecting duplicates easier during validation.
        EqualsVerifier.forClass(AssessmentReference.class)
            .allFieldsShouldBeUsedExcept("resolver", "id", "sharedId").verify();
    }

    @Test
    public void succeeds() throws Exception {
        ConfigResolver resolver = mockConfigResolver(UAT, "ws");
        AssessmentReference ref = new AssessmentReference(resolver, "oneGuid", "id", "sharedId");
        
        assertEquals(ref.getGuid(), "oneGuid");
        assertEquals(ref.getId(), "id");
        assertEquals(ref.getSharedId(), "sharedId");
        assertEquals(ref.getConfigHref(), 
                "https://ws-uat.bridge.org/v1/assessments/oneGuid/config");
    }
    
    @Test
    public void noIdentifiers() {
        ConfigResolver resolver = mockConfigResolver(LOCAL, "ws");
        AssessmentReference ref = new AssessmentReference(resolver, "oneGuid", null, null);
        
        assertEquals(ref.getGuid(), "oneGuid");
        assertNull(ref.getId());
        assertNull(ref.getSharedId());
        assertEquals(ref.getConfigHref(), 
                "http://ws-local.bridge.org/v1/assessments/oneGuid/config");
    }
    
    @Test
    public void noGuid() {
        AssessmentReference ref = new AssessmentReference(null, null, null);
        assertNull(ref.getConfigHref());
    }
    
    @Test
    public void canSerialise() throws Exception {
        ConfigResolver resolver = mockConfigResolver(LOCAL, "ws");
        AssessmentReference ref = new AssessmentReference(resolver, "oneGuid", "id", "sharedId");
        
        JsonNode node = BridgeObjectMapper.get().valueToTree(ref);
        assertEquals(node.get("guid").textValue(), "oneGuid");
        assertEquals(node.get("id").textValue(), "id");
        assertEquals(node.get("sharedId").textValue(), "sharedId");
        assertEquals(node.get("configHref").textValue(), 
            "http://ws-local.bridge.org/v1/assessments/oneGuid/config");
        assertEquals(node.get("type").textValue(), "AssessmentReference");
        
        AssessmentReference deser = BridgeObjectMapper.get().readValue(
                node.toString(), AssessmentReference.class);
        assertEquals(deser, ref);
    }
    
}
