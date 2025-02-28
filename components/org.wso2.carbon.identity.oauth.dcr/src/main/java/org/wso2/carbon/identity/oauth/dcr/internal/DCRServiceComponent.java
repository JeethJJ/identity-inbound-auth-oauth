/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth.dcr.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityRequestFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponseFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityProcessor;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.configuration.mgt.core.ConfigurationManager;
import org.wso2.carbon.identity.oauth.common.token.bindings.TokenBinderInfo;
import org.wso2.carbon.identity.oauth.dcr.DCRConfigurationMgtService;
import org.wso2.carbon.identity.oauth.dcr.DCRConfigurationMgtServiceImpl;
import org.wso2.carbon.identity.oauth.dcr.factory.HttpRegistrationResponseFactory;
import org.wso2.carbon.identity.oauth.dcr.factory.HttpUnregistrationResponseFactory;
import org.wso2.carbon.identity.oauth.dcr.factory.RegistrationRequestFactory;
import org.wso2.carbon.identity.oauth.dcr.factory.UnregistrationRequestFactory;
import org.wso2.carbon.identity.oauth.dcr.handler.AdditionalAttributeFilter;
import org.wso2.carbon.identity.oauth.dcr.handler.RegistrationHandler;
import org.wso2.carbon.identity.oauth.dcr.handler.UnRegistrationHandler;
import org.wso2.carbon.identity.oauth.dcr.processor.DCRProcessor;
import org.wso2.carbon.identity.oauth.dcr.service.DCRMService;
import org.wso2.carbon.identity.oauth2.token.bindings.TokenBinder;
import org.wso2.carbon.identity.organization.management.service.OrganizationManager;

/**
 * OAuth DCRM service component.
 * This was deprecated as part of deprecating the legacy identity/register DCR endpoint.
 * The recommendation is to use /identity/oauth2/dcr/v1.1 instead.
 */
@Component(
        name = "identity.oauth.dcr",
        immediate = true
)
public class DCRServiceComponent {

    private static final Log log = LogFactory.getLog(DCRServiceComponent.class);

    @SuppressWarnings("unused")
    protected void activate(ComponentContext componentContext) {

        try {

            componentContext.getBundleContext().registerService(IdentityProcessor.class.getName(),
                    new DCRProcessor(), null);

            componentContext.getBundleContext().registerService(HttpIdentityRequestFactory.class.getName(),
                    new RegistrationRequestFactory(), null);

            componentContext.getBundleContext().registerService(HttpIdentityResponseFactory.class.getName(),
                    new HttpRegistrationResponseFactory(), null);

            componentContext.getBundleContext().registerService(HttpIdentityRequestFactory.class.getName(),
                    new UnregistrationRequestFactory(), null);
            componentContext.getBundleContext().registerService(HttpIdentityResponseFactory.class.getName(),
                    new HttpUnregistrationResponseFactory(), null);

            componentContext.getBundleContext().registerService(RegistrationHandler.class.getName(),
                    new RegistrationHandler(), null);

            componentContext.getBundleContext().registerService(UnRegistrationHandler.class.getName(),
                    new UnRegistrationHandler(), null);
            componentContext.getBundleContext().registerService(DCRMService.class.getName(),
                    new DCRMService(), null);
            componentContext.getBundleContext().registerService(DCRConfigurationMgtService.class.getName(),
                    new DCRConfigurationMgtServiceImpl(), null);
        } catch (Throwable e) {
            log.error("Error occurred while activating DCRServiceComponent", e);
        }
    }

    @SuppressWarnings("unused")
    protected void deactivate(ComponentContext componentContext) {

        if (log.isDebugEnabled()) {
            log.debug("Stopping DCRServiceComponent");
        }
    }

    /**
     * Sets RegistrationHandler Service.
     *
     * @param registrationHandler An instance of RegistrationHandler
     */
    @Reference(
            name = "identity.oauth.dcr.handler.register",
            service = RegistrationHandler.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistrationHandler"
    )
    protected void setRegistrationHandler(RegistrationHandler registrationHandler) {

        if (log.isDebugEnabled()) {
            log.debug("Setting RegistrationHandler Service");
        }
        DCRDataHolder.getInstance().
                getRegistrationHandlerList().add(registrationHandler);
    }

    /**
     * Unsets RegistrationHandler Service.
     *
     * @param registrationHandler An instance of RegistrationHandler
     */
    protected void unsetRegistrationHandler(RegistrationHandler registrationHandler) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting RegistrationHandler.");
        }
        DCRDataHolder.getInstance().
                getRegistrationHandlerList().add(null);
    }

    /**
     * Sets DCRManagementService Service.
     *
     * @param unRegistrationHandler An instance of DCRManagementService
     */
    @Reference(
            name = "identity.oauth.dcr.handler.unregister",
            service = UnRegistrationHandler.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetUnRegistrationHandler"
    )
    protected void setUnRegistrationHandler(UnRegistrationHandler
                                                    unRegistrationHandler) {

        if (log.isDebugEnabled()) {
            log.debug("Setting DCRManagementService.");
        }
        DCRDataHolder.getInstance().getUnRegistrationHandlerList().add(unRegistrationHandler);
    }

    /**
     * Unsets UnRegistrationHandler.
     *
     * @param unRegistrationHandler An instance of UnRegistrationHandler
     */
    protected void unsetUnRegistrationHandler(UnRegistrationHandler unRegistrationHandler) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting UnRegistrationHandler.");
        }
        DCRDataHolder.getInstance().getUnRegistrationHandlerList().add(null);
    }

    /**
     * Sets ApplicationManagement Service.
     *
     * @param applicationManagementService An instance of ApplicationManagementService
     */
    @Reference(
            name = "application.mgt.service",
            service = ApplicationManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationManagementService"
    )
    protected void setApplicationManagementService(ApplicationManagementService applicationManagementService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting ApplicationManagement Service");
        }
        DCRDataHolder.getInstance().
                setApplicationManagementService(applicationManagementService);
    }

    /**
     * Unsets ApplicationManagement Service.
     *
     * @param applicationManagementService An instance of ApplicationManagementService
     */
    protected void unsetApplicationManagementService(ApplicationManagementService applicationManagementService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting ApplicationManagement.");
        }
        DCRDataHolder.getInstance().setApplicationManagementService(null);
    }

    @Reference(name = "token.binding.service",
            service = TokenBinderInfo.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetTokenBinderInfo")
    protected void setTokenBinderInfo(TokenBinderInfo tokenBinderInfo) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the token binder for: " + tokenBinderInfo.getBindingType());
        }
        if (tokenBinderInfo instanceof TokenBinder) {
            DCRDataHolder.getInstance().addTokenBinder((TokenBinder) tokenBinderInfo);
        }
    }
    protected void unsetTokenBinderInfo(TokenBinderInfo tokenBinderInfo) {

        if (log.isDebugEnabled()) {
            log.debug("Un-setting the token binder for: " + tokenBinderInfo.getBindingType());
        }
        if (tokenBinderInfo instanceof TokenBinder) {
            DCRDataHolder.getInstance().removeTokenBinder((TokenBinder) tokenBinderInfo);
        }
    }

    /**
     * Set the ConfigurationManager.
     *
     * @param configurationManager The {@code ConfigurationManager} instance.
     */
    @Reference(
            name = "resource.configuration.manager",
            service = ConfigurationManager.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterConfigurationManager"
    )
    protected void registerConfigurationManager(ConfigurationManager configurationManager) {

        log.debug("Registering the ConfigurationManager in DCR Service Component.");
        DCRDataHolder.getInstance().setConfigurationManager(configurationManager);
    }


    /**
     * Unset the ConfigurationManager.
     *
     * @param configurationManager The {@code ConfigurationManager} instance.
     */
    protected void unregisterConfigurationManager(ConfigurationManager configurationManager) {

        log.debug("Unregistering the ConfigurationManager in DCR Service Component.");
        DCRDataHolder.getInstance().setConfigurationManager(null);
    }

    @Reference(
            name = "organization.service",
            service = OrganizationManager.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetOrganizationManager"
    )
    protected void setOrganizationManager(OrganizationManager organizationManager) {

        DCRDataHolder.getInstance().setOrganizationManager(organizationManager);
        log.debug("Set the organization management service.");
    }

    protected void unsetOrganizationManager(OrganizationManager organizationManager) {

        DCRDataHolder.getInstance().setOrganizationManager(null);
        log.debug("Unset organization management service.");
    }

    @Reference(name = "identity.oauth.dcr.attribute.filter",
            service = AdditionalAttributeFilter.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetAdditionalAttributeFilter")
    protected void setAdditionalAttributeFilter(AdditionalAttributeFilter additionalAttributeFilter) {

        DCRDataHolder.getInstance().setAdditionalAttributeFilter(additionalAttributeFilter);
    }

    protected void unsetAdditionalAttributeFilter(AdditionalAttributeFilter tokenBinderInfo) {

        DCRDataHolder.getInstance().setAdditionalAttributeFilter(null);
    }
}
