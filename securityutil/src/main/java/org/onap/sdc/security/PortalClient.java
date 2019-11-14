package org.onap.sdc.security;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.sdc.security.logs.elements.LogFieldsMdcHandler;
import org.onap.sdc.security.logs.enums.EcompLoggerErrorCode;
import org.onap.sdc.security.logs.wrappers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Base64;
import static org.onap.portalsdk.core.onboarding.util.CipherUtil.decryptPKC;
import static org.onap.sdc.security.utils.SecurityLogsUtils.PORTAL_TARGET_ENTITY;
import static org.onap.sdc.security.utils.SecurityLogsUtils.fullOptionalData;


@Component/*("portalUtils")*/
public class PortalClient {

    private static final Logger log = Logger.getLogger(PortalClient.class.getName());
    private final CloseableHttpClient httpClient;
    private static final String GET_ROLES_PORTAL_URL = "/v4/user/%s";
    private static final String UEB_KEY = "uebkey";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private final PortalConfiguration portalConfiguration;
    private static final String RECEIVED_NULL_ROLES = "Received null roles for user";
    private static final String RECEIVED_MULTIPLE_ROLES = "Received multiple roles for user {}";
    private static final String RECEIVED_MULTIPLE_ROLES2 = "Received multiple roles for user";

/*    @Autowired
    public PortalClient(CloseableHttpClient httpClient) {
        try {
            this.portalConfiguration = new PortalConfiguration();
        } catch (org.onap.portalsdk.core.onboarding.exception.CipherUtilException e) {
            throw new RestrictionAccessFilterException(e);
        }
        this.httpClient = httpClient;
    }*/

    @Autowired
    public PortalClient(CloseableHttpClient httpClient, IPortalConfiguration portalConfiguration) {
        try {
            this.portalConfiguration = new PortalConfiguration(portalConfiguration);
        } catch (org.onap.portalsdk.core.onboarding.exception.CipherUtilException e) {
            throw new RestrictionAccessFilterException(e);
        }
        this.httpClient = httpClient;
    }

    public String fetchUserRolesFromPortal(String userId) {
        String fetchedUserRoleFromPortal;
        try {
            EcompUser ecompUser = extractObjectFromResponseJson(getResponseFromPortal(userId));
            log.debug("GET USER ROLES response for user {}: {}", userId, ecompUser);
            checkIfSingleRoleProvided(ecompUser);
            fetchedUserRoleFromPortal = ecompUser.getRoles().stream().findFirst().get().getName();
        } catch (IOException | PortalAPIException e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(),
                    fullOptionalData(PORTAL_TARGET_ENTITY, "/fetchRolesFromPortal"),"GET USER ROLES from portal failed: {}", e.getMessage());
            log.debug("Fetching user roles from Portal failed", e);
            throw new RestrictionAccessFilterException(e);
        }
        return fetchedUserRoleFromPortal;
    }



    public static void checkIfSingleRoleProvided(EcompUser user) throws PortalAPIException {
        if(user.getRoles() == null) {
            log.debug(RECEIVED_NULL_ROLES, user);
            //BeEcompErrorManager.getInstance().logInvalidInputError(CHECK_ROLES, RECEIVED_NULL_ROLES, BeEcompErrorManager.ErrorSeverity.ERROR);
            throw new PortalAPIException(RECEIVED_NULL_ROLES + user);
        }else if(user.getRoles().size() > 1) {
            log.debug(RECEIVED_MULTIPLE_ROLES, user);
            //BeEcompErrorManager.getInstance().logInvalidInputError(CHECK_ROLES, RECEIVED_MULTIPLE_ROLES2, BeEcompErrorManager.ErrorSeverity.ERROR);
            throw new PortalAPIException(RECEIVED_MULTIPLE_ROLES2 + user);
        }
    }

    private String getResponseFromPortal(String userId) throws IOException {
        HttpGet httpGet = createHttpGetRequest(userId);
        CloseableHttpResponse httpResponse = this.httpClient.execute(httpGet);
        //Json extraction is handled inside this function due to IO Stream issues.
        JSONObject jsonFromResponse = (JSONObject) JSONValue.parse(EntityUtils.toString(httpResponse.getEntity()));
        httpResponse.close();
        return jsonFromResponse.toString();
    }

    private EcompUser extractObjectFromResponseJson(String jsonFromResponse) {
        return RepresentationUtils.fromRepresentation(jsonFromResponse, EcompUser.class);
    }

    private HttpGet createHttpGetRequest(String userId) {
        final String url = String.format(portalConfiguration.getEcompPortalRestURL() + GET_ROLES_PORTAL_URL, userId);
        log.debug("Going to execute an http get to the following URL: {}", url);

        HttpGet httpGet = new HttpGet(url);
        String encodedBasicAuthCred = Base64.getEncoder()
                .encodeToString((portalConfiguration.getDecryptedPortalUser() + ":" +
                        portalConfiguration.getDecryptedPortalPassword())
                        .getBytes());
        httpGet.setHeader(UEB_KEY, portalConfiguration.getUebKey());
        httpGet.setHeader(AUTHORIZATION, "Basic " + encodedBasicAuthCred);
        httpGet.setHeader(CONTENT_TYPE_HEADER, "application/json");
        return httpGet;
    }

    private static class PortalConfiguration {
        private static final String PROPERTY_NOT_SET = "%s property value is not set in portal.properties file";
        private String decryptedPortalUser;
        private String decryptedPortalPassword;
        private String ecompPortalRestURL;
        private String decryptedPortalAppName;
        private String uebKey;

        private PortalConfiguration() throws org.onap.portalsdk.core.onboarding.exception.CipherUtilException {
            this.decryptedPortalUser = decryptPKC(getPortalProperty(PortalPropertiesEnum.USER.value()));
            this.decryptedPortalPassword = decryptPKC(getPortalProperty(PortalPropertiesEnum.PASSWORD.value()));
            this.decryptedPortalAppName = decryptPKC(getPortalProperty(PortalPropertiesEnum.APP_NAME.value()));
            this.ecompPortalRestURL = getPortalProperty(PortalPropertiesEnum.ECOMP_REST_URL.value());
            this.uebKey = getPortalProperty(PortalPropertiesEnum.UEB_APP_KEY.value());

        }

        private PortalConfiguration(IPortalConfiguration portalConfiguration) throws org.onap.portalsdk.core.onboarding.exception.CipherUtilException {
            this.decryptedPortalUser = decryptPKC(portalConfiguration.getPortalUser());
            this.decryptedPortalPassword = decryptPKC(portalConfiguration.getPortalPass());
            this.decryptedPortalAppName = decryptPKC(portalConfiguration.getPortalAppName());
            this.ecompPortalRestURL = portalConfiguration.getEcompRestUrl();
            this.uebKey = portalConfiguration.getUebAppKey();

        }

        public static String getPropertyNotSet() {
            return PROPERTY_NOT_SET;
        }

        public String getDecryptedPortalUser() {
            return decryptedPortalUser;
        }

        public String getDecryptedPortalPassword() {
            return decryptedPortalPassword;
        }

        public String getEcompPortalRestURL() {
            return ecompPortalRestURL;
        }

        public String getDecryptedPortalAppName() {
            return decryptedPortalAppName;
        }

        public String getUebKey() {
            return uebKey;
        }

        @VisibleForTesting
        String getPortalProperty(String key) {
            String value = PortalApiProperties.getProperty(key);
            if (StringUtils.isEmpty(value)) {
                throw new InvalidParameterException(String.format(PROPERTY_NOT_SET, key));
            }
            return value;
        }

    }

    public enum PortalPropertiesEnum {
        APP_NAME("portal_app_name"),
        ECOMP_REST_URL("ecomp_rest_url"),
        PASSWORD("portal_pass"),
        UEB_APP_KEY("ueb_app_key"),
        USER("portal_user");

        private final String value;

        PortalPropertiesEnum(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

}


