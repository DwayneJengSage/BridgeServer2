package org.sagebionetworks.bridge.services;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.sagebionetworks.bridge.dao.FPHSExternalIdentifierDao;
import org.sagebionetworks.bridge.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.models.accounts.ExternalIdentifier;
import org.sagebionetworks.bridge.models.accounts.FPHSExternalIdentifier;
import org.sagebionetworks.bridge.models.studies.Enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FPHSService {
    
    private FPHSExternalIdentifierDao fphsDao;
    private AccountService accountService;

    @Autowired
    final void setFPHSExternalIdentifierDao(FPHSExternalIdentifierDao dao) {
        this.fphsDao = dao;
    }
    @Autowired
    final void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    public void verifyExternalIdentifier(ExternalIdentifier externalId) {
        checkNotNull(externalId);
        
        if (isBlank(externalId.getIdentifier())) {
            throw new InvalidEntityException(externalId, "ExternalIdentifier is not valid");
        }
        // Throws exception if not verified
        fphsDao.verifyExternalId(externalId);
    }

    public void registerExternalIdentifier(String appId, String healthCode, ExternalIdentifier externalId) {
        checkNotNull(appId);
        checkNotNull(healthCode);
        checkNotNull(externalId);
        
        if (isBlank(externalId.getIdentifier())) {
            throw new InvalidEntityException(externalId, "ExternalIdentifier is not valid");
        }
        verifyExternalIdentifier(externalId);
        
        fphsDao.registerExternalId(externalId);

        accountService.editAccount(appId, healthCode, account -> {
            Enrollment enrollment = Enrollment.create(appId, "harvard", account.getId(), externalId.getIdentifier());
            account.getDataGroups().add("football_player");
            account.getEnrollments().add(enrollment);
        });
    }
    
    /**
     * Get all FPHS identifiers along with information about which ones have been used to register.
     * 
     * @return
     * @throws Exception
     */
    public List<FPHSExternalIdentifier> getExternalIdentifiers() {
        return fphsDao.getExternalIds();
    }
    
    /**
     * Add new external identifiers to the database. This will not overwrite the registration status of existing
     * external IDs.
     * 
     * @param externalIds
     * @throws Exception
     */
    public void addExternalIdentifiers(List<FPHSExternalIdentifier> externalIds) {
        checkNotNull(externalIds);
        fphsDao.addExternalIds(externalIds);
    }
}
