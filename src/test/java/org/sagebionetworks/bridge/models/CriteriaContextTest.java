package org.sagebionetworks.bridge.models;

import org.testng.annotations.Test;

import org.sagebionetworks.bridge.TestConstants;
import org.sagebionetworks.bridge.models.accounts.AccountId;

import static org.sagebionetworks.bridge.TestConstants.TEST_APP_ID;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CriteriaContextTest {
    
    private static final ClientInfo CLIENT_INFO = ClientInfo.parseUserAgentString("app/20");
    private static final String USER_ID = "user-id";
    
    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(CriteriaContext.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void defaultsClientInfo() {
        CriteriaContext context = new CriteriaContext.Builder()
                .withUserId(USER_ID)
                .withAppId(TEST_APP_ID).build();
        assertEquals(context.getClientInfo(), ClientInfo.UNKNOWN_CLIENT);
        assertEquals(context.getLanguages(), ImmutableList.of());
        assertEquals(context.getUserDataGroups(), ImmutableList.of());
        assertEquals(context.getUserStudyIds(), ImmutableList.of());
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void requiresAppId() {
        new CriteriaContext.Builder().withUserId(USER_ID).build();
    }
    
    @Test
    public void builderWorks() {
        CriteriaContext context = new CriteriaContext.Builder()
                .withAppId(TEST_APP_ID)
                .withUserId(USER_ID)
                .withClientInfo(CLIENT_INFO)
                .withUserDataGroups(TestConstants.USER_DATA_GROUPS)
                .withUserStudyIds(TestConstants.USER_STUDY_IDS).build();
        
        // There are defaults
        assertEquals(context.getClientInfo(), CLIENT_INFO);
        assertEquals(context.getUserDataGroups(), TestConstants.USER_DATA_GROUPS);
        assertEquals(context.getUserStudyIds(), TestConstants.USER_STUDY_IDS);
        
        CriteriaContext copy = new CriteriaContext.Builder().withContext(context).build();
        assertEquals(copy.getClientInfo(), CLIENT_INFO);
        assertEquals(copy.getAppId(), TEST_APP_ID);
        assertEquals(copy.getUserId(), USER_ID);
        assertEquals(copy.getUserDataGroups(), TestConstants.USER_DATA_GROUPS);
        assertEquals(copy.getUserStudyIds(), TestConstants.USER_STUDY_IDS);
    }
    
    @Test
    public void contextHasAccountId() {
        CriteriaContext context = new CriteriaContext.Builder()
                .withAppId(TEST_APP_ID)
                .withUserId(USER_ID).build();
        
        AccountId accountId = context.getAccountId();
        assertEquals(accountId.getAppId(), TEST_APP_ID);
        assertEquals(accountId.getId(), USER_ID);
    }
}
