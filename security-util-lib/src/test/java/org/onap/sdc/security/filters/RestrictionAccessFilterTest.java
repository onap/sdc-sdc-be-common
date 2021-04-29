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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onap.sdc.security.AuthenticationCookie;
import org.onap.sdc.security.ISessionValidationFilterConfiguration;
import org.onap.sdc.security.IUsersThreadLocalHolder;
import org.onap.sdc.security.PortalClient;
import org.onap.sdc.security.RedirectException;


public class RestrictionAccessFilterTest {

    private static final String userId = "abc123";

    private final ISessionValidationFilterConfiguration configuration = Mockito
        .mock(ISessionValidationFilterConfiguration.class);

    private final IUsersThreadLocalHolder threadLocalUtils = Mockito.mock(IUsersThreadLocalHolder.class);

    private final AuthenticationCookie authenticationCookie = Mockito.mock(AuthenticationCookie.class);

    private final PortalClient portalClient = Mockito.mock(PortalClient.class);

    private RestrictionAccessFilter filter;

    @BeforeEach
    public void setUp() {
        mockCreateFilter(configuration, threadLocalUtils, portalClient);
        assertNotNull(filter);
    }

    private void mockCreateFilter(ISessionValidationFilterConfiguration sessionConfig,
        IUsersThreadLocalHolder threadLocalUtils, PortalClient portalClient) {
        filter = new RestrictionAccessFilter(sessionConfig, threadLocalUtils, portalClient);
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenUsersDifferent() {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(RestrictionAccessFilter.CSP_USER_ID, "user1");
        assertThrows(
            RedirectException.class,
            () -> filter.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenUserRolesDifferent() {
        when(authenticationCookie.getUserID()).thenReturn(userId);
        RestrictionAccessFilter spyFilter2 = spy(filter);
        HashSet<String> roles = new HashSet<>();
        roles.add("b");
        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        when(authenticationCookie.getRoles()).thenReturn(roles);
        assertThrows(
            RedirectException.class,
            () -> spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenUserRolesDisappearInCookie() {
        when(authenticationCookie.getUserID()).thenReturn(userId);
        RestrictionAccessFilter spyFilter2 = spy(filter);
        HashSet<String> roles = new HashSet<>();
        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        when(authenticationCookie.getRoles()).thenReturn(roles);
        assertThrows(
            RedirectException.class,
            () -> spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenUserRolesRetrievedFromPortalAndMatch() {
        when(authenticationCookie.getUserID()).thenReturn(userId);
        RestrictionAccessFilter spyFilter2 = spy(filter);
        HashSet<String> roles = new HashSet<>();
        roles.add("DESIGNER");

        when(authenticationCookie.getRoles()).thenReturn(roles);
        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        assertThrows(
            RedirectException.class,
            () -> spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );

    }

    @Test
    public void authorizeUserOnSessionExpirationWhenUserRolesRetrievedFromPortalAndEmpty() {
        HashSet<String> roles = new HashSet<>();
        roles.add("b");
        when(authenticationCookie.getUserID()).thenReturn(userId);
        when(authenticationCookie.getRoles()).thenReturn(roles);
        RestrictionAccessFilter spyFilter2 = spy(filter);

        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        assertThrows(
            RedirectException.class,
            () -> spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenUserRolesCantBeRetrievedFromPortal() {
        HashSet<String> roles = new HashSet<>();
        roles.add("b");
        when(authenticationCookie.getUserID()).thenReturn(userId);
        when(authenticationCookie.getRoles()).thenReturn(roles);
        RestrictionAccessFilter spyFilter2 = spy(filter);

        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        assertThrows(
            RedirectException.class,
            () -> spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenCspUserCookieIsNull() {
        when(authenticationCookie.getUserID()).thenReturn(userId);

        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, null)};
        assertThrows(
            RedirectException.class,
            () -> filter.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void authorizeUserOnSessionExpirationWhenCookieNotFound() {
        assertNotNull(filter);
        Cookie[] cookies = new Cookie[]{new Cookie("someCookie", userId)};
        assertThrows(
            RedirectException.class,
            () -> filter.authorizeUserOnSessionExpiration(authenticationCookie, cookies)
        );
    }

    @Test
    public void getCspUserIdWhenMoreThanOneUserIdExists() throws RedirectException {
        Cookie[] cookies = new Cookie[]{
            new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId),
            new Cookie(RestrictionAccessFilter.CSP_USER_ID, "other")};
        assertEquals(userId, filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID));
    }

    @Test
    public void getCspUserIdWhenUserIdIsNull() throws RedirectException {
        Cookie[] cookies = new Cookie[]{new Cookie(RestrictionAccessFilter.CSP_USER_ID, null)};
        assertNull(filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID));
    }

    @Test
    public void getCspUserIdWhenUserIdCookieNotFound() {
        Cookie[] cookies = new Cookie[]{new Cookie("someName", "someValue")};
        assertThrows(
            RedirectException.class,
            () -> filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID)
        );
    }

    @Test
    public void getCspUserIdWhenNoCookiesFound() {
        Cookie[] cookies = new Cookie[]{};
        assertThrows(
            RedirectException.class,
            () -> filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID)
        );
    }

    @Test
    public void getCspUserIdWhenCookiesNull() {
        assertThrows(
            RedirectException.class,
            () -> filter.getCookieValue(null, RestrictionAccessFilter.CSP_USER_ID)
        );
    }


}
