/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.security.filters;

import org.onap.sdc.security.AuthenticationCookie;
import org.onap.sdc.security.ISessionValidationFilterConfiguration;
import org.onap.sdc.security.RedirectException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleFilter extends SessionValidationFilter {

    static final String FAILED_ON_USER_AUTH = "failedOnUserAuth";
    static final String FAILED_ON_ROLE = "failedOnRole";

    private static class Configuration implements ISessionValidationFilterConfiguration {

        private String securityKey = "AGLDdG4D04BKm2IxIWEr8o==";
        private long maxSessionTimeOut = 24*60*60*1000;
        private long sessionIdleTimeOut = 60*60*1000;
        private String redirectURL = "http://portal.api.simpledemo.onap.org:8989/ECOMPPORTAL/login.htm";
        private List<String> excludedUrls = new ArrayList<>(Arrays.asList("/config","/configmgr","/rest","/kibanaProxy","/healthcheck","/upload.*"));
        private String cookieName = "kuku";
        private final String cookieDomain = "";
        private final String cookiePath = "/";
        private boolean isCookieHttpOnly = true;

        private static final Configuration instance = new Configuration();


        private Configuration() {
        }

        public static Configuration getInstance(){
             return instance;
        }

        // --- set method only for tests START ---
        private void setSecurityKey(String securityKey) {
            this.securityKey = securityKey;
        }

        private void setMaxSessionTimeOut(long maxSessionTimeOut) {
            this.maxSessionTimeOut = maxSessionTimeOut;
        }

        private void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        private void setRedirectURL(String redirectURL) {
            this.redirectURL = redirectURL;
        }

        private void setExcludedUrls(List<String> excludedUrls) {
            this.excludedUrls = excludedUrls;
        }
        // --- set method only for tests START ---

        @Override
        public String getSecurityKey() {
            return securityKey;
        }

        @Override
        public long getMaxSessionTimeOut() {
            return maxSessionTimeOut;
        }

        @Override
        public long getSessionIdleTimeOut() {
            return sessionIdleTimeOut;
        }

        @Override
        public String getCookieName() {
            return cookieName;
        }

        @Override
        public String getCookieDomain() {
            return cookieDomain;
        }

        @Override
        public String getCookiePath() {
            return cookiePath;
        }

        @Override
        public boolean isCookieHttpOnly() {
            return isCookieHttpOnly;
        }

        @Override
        public String getRedirectURL() {
            return redirectURL;
        }

        @Override
        public List<String> getExcludedUrls() {
            return excludedUrls;
        }

    }

    // --- set methods only for tests START ---
    public void setSecurityKey(String securityKey) {
        Configuration.getInstance().setSecurityKey(securityKey);
    }

    public void setMaxSessionTimeOut(long maxSessionTimeOut) {
        Configuration.getInstance().setMaxSessionTimeOut(maxSessionTimeOut);
    }

    public void setCookieName(String cookieName) {
        Configuration.getInstance().setCookieName(cookieName);
    }

    public void setRedirectURL(String redirectURL) {
        Configuration.getInstance().setRedirectURL(redirectURL);
    }

    public void setExcludedUrls(List<String> excludedUrls) {
        Configuration.getInstance().setExcludedUrls(excludedUrls);
    }
    // --- set methods only for tests END ---

    @Override
    public ISessionValidationFilterConfiguration getFilterConfiguration() {
        return Configuration.getInstance();
    }

    @Override
    protected Cookie addRoleToCookie(Cookie updatedCookie) {
        return updatedCookie;
    }

    @Override
    protected void authorizeUserOnSessionExpiration(AuthenticationCookie authenticationCookie, Cookie[] cookies) throws RedirectException {
        if (FAILED_ON_USER_AUTH.equals(authenticationCookie.getUserID())) {
            throw new RedirectException();
        }
    }

    @Override
    protected void handleRedirectException(HttpServletResponse httpResponse) throws IOException {
        //do nothing in this implementation
    }

}


