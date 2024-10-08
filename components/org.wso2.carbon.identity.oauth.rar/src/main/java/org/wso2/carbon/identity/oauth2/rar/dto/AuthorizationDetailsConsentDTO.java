package org.wso2.carbon.identity.oauth2.rar.dto;

import org.wso2.carbon.identity.oauth2.rar.model.AuthorizationDetail;

/**
 * Data Transfer Object (DTO) for representing authorization details along with consent information.
 * This class extends {@link AuthorizationDetailsDTO} to include additional fields for consent ID and consent status.
 */
public class AuthorizationDetailsConsentDTO extends AuthorizationDetailsDTO {

    final String consentId;
    final boolean isConsentActive;

    /**
     * Constructs an {@link AuthorizationDetailsConsentDTO} with all required fields.
     *
     * @param id                      the ID of the authorization detail DTO.
     * @param consentId               the consent ID associated with the authorization detail.
     * @param typeId                  the type ID of the authorization detail.
     * @param authorizationDetailJson the JSON string of the authorization detail.
     * @param isConsentActive         the consent status.
     * @param tenantId                the tenant ID.
     */
    public AuthorizationDetailsConsentDTO(final String id, final String consentId, final String typeId,
                                          final String authorizationDetailJson,
                                          final boolean isConsentActive, final int tenantId) {

        super(id, typeId, authorizationDetailJson, tenantId);
        this.consentId = consentId;
        this.isConsentActive = isConsentActive;
    }

    /**
     * Constructs an {@link AuthorizationDetailsConsentDTO} with essential fields.
     *
     * @param consentId           the consent ID associated with the authorization detail.
     * @param authorizationDetail the {@link AuthorizationDetail} object.
     * @param isConsentActive     the consent status.
     * @param tenantId            the tenant ID.
     */
    public AuthorizationDetailsConsentDTO(final String consentId,
                                          final AuthorizationDetail authorizationDetail,
                                          final boolean isConsentActive, final int tenantId) {

        super(authorizationDetail, tenantId);
        this.consentId = consentId;
        this.isConsentActive = isConsentActive;
    }

    /**
     * Checks if the consent is active.
     *
     * @return {@code true} if consent is active, {@code false} otherwise.
     */
    public boolean isConsentActive() {
        return isConsentActive;
    }

    /**
     * Gets the consent ID associated with the authorization detail.
     *
     * @return the consent ID.
     */
    public String getConsentId() {
        return consentId;
    }
}
