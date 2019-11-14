package org.onap.sdc.security.filters;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.sdc.security.*;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestrictionAccessFilterTest {

    private static final String jsonResponseFromPortal = "{\"orgId\":null,\"managerId\":null,\"firstName\":\"NAME\",\"middleInitial\":null,\"lastName\":\"FAMILY\",\"phone\":null,\"email\":\"abc123@test.com\",\"hrid\":null,\"orgUserId\":\"abc123\",\"orgCode\":null,\"orgManagerUserId\":null,\"jobTitle\":null," +
            "\"loginId\":\"abc123\",\"active\":true,\"roles\":[%s]}";
    private static final String rolesJson = "{\"id\":\"1234\",\"name\":\"designer\",\"roleFunctions\":[\"read\",\"write\"]}";

    private static final String userId = "abc123";

    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private ISessionValidationFilterConfiguration configuration;
    @Mock
    private ISessionValidationCookieConfiguration cookieConfig;
    @Mock
    private IUsersThreadLocalHolder threadLocalUtils;
    @Mock
    private AuthenticationCookie authenticationCookie;
    @Mock
    private PortalClient portalClient;

    private RestrictionAccessFilter filter;

    @Before
    public void setUp() {
        mockCreateFilter(configuration, threadLocalUtils, portalClient);
        assertNotNull(filter);
    }

    private void mockCreateFilter(ISessionValidationFilterConfiguration sessionConfig, IUsersThreadLocalHolder threadLocalUtils, PortalClient portalClient) {
        filter = new RestrictionAccessFilter(sessionConfig, threadLocalUtils, portalClient);
    }

//    @Test(expected = InvalidParameterException.class)
//    public void filterConfigurationObjectCreationFailedWhenPropertyIsNotSet() {
//        when(configuration.getAuthCookie()).thenReturn(cookieConfig);
//        when(cookieConfig.getRedirectURL()).thenReturn("does_not_exist");
//        filter = new RestrictionAccessFilter(configuration, portalClient, threadLocalUtils);
//    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenUsersDifferent() throws RedirectException {
        Cookie[] cookies = new Cookie [1] ;
        cookies[0] = new Cookie(RestrictionAccessFilter.CSP_USER_ID, "user1");
        filter.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenUserRolesDifferent() throws RedirectException, IOException {
        when(authenticationCookie.getUserID()).thenReturn(userId);
        RestrictionAccessFilter spyFilter2 = spy(filter);
        HashSet<String> roles = new HashSet<>();
        roles.add("b");
        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        when(authenticationCookie.getRoles()).thenReturn(roles);
        spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenUserRolesDisappearInCookie() throws RedirectException, IOException {
        when(authenticationCookie.getUserID()).thenReturn(userId);
        RestrictionAccessFilter spyFilter2 = spy(filter);
        HashSet<String> roles = new HashSet<>();
        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        when(authenticationCookie.getRoles()).thenReturn(roles);
        spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenUserRolesRetrievedFromPortalAndMatch() throws RedirectException, IOException {
        when(authenticationCookie.getUserID()).thenReturn(userId);
        PortalClient spyFilter = spy(portalClient);
        RestrictionAccessFilter spyFilter2 = spy(filter);
        HashSet<String> roles = new HashSet<>();
        roles.add("DESIGNER");

        when(authenticationCookie.getRoles()).thenReturn(roles);
        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies);

    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenUserRolesRetrievedFromPortalAndEmpty() throws RedirectException, IOException {
        HashSet<String> roles = new HashSet<>();
        roles.add("b");
        when(authenticationCookie.getUserID()).thenReturn(userId);
        when(authenticationCookie.getRoles()).thenReturn(roles);
        PortalClient spyFilter = spy(portalClient);
        RestrictionAccessFilter spyFilter2 = spy(filter);

        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenUserRolesCantBeRetrievedFromPortal() throws RedirectException, IOException {
        HashSet<String> roles = new HashSet<>();
        roles.add("b");
        when(authenticationCookie.getUserID()).thenReturn(userId);
        when(authenticationCookie.getRoles()).thenReturn(roles);
        RestrictionAccessFilter spyFilter2 = spy(filter);

        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
        spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

//    @Test (expected = RedirectException.class)
//    public void authorizeUserOnSessionExpirationWhenHttpRequestFailed() throws RedirectException, IOException, RestrictionAccessFilterException {
//        HashSet<String> roles = new HashSet<>();
//        roles.add("b");
//        when(authenticationCookie.getUserID()).thenReturn(userId);
//        when(authenticationCookie.getRoles()).thenReturn(roles);
////      PortalClient spyFilter = spy(portalClient);
//        RestrictionAccessFilter spyFilter2 = spy(filter);
////       when(spyFilter.fetchUserRolesFromPortal()*/
//        doThrow(IOException.class).when(portalClient).fetchUserRolesFromPortal(eq(userId));
//        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId)};
//        spyFilter2.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
//    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenCspUserCookieIsNull() throws RedirectException, IOException {
        when(authenticationCookie.getUserID()).thenReturn(userId);

        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, null)};
        filter.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

    @Test (expected = RedirectException.class)
    public void authorizeUserOnSessionExpirationWhenCookieNotFound() throws RedirectException, IOException {
        assertNotNull(filter);
        Cookie[] cookies = new Cookie [] {new Cookie("someCookie", userId)};
        filter.authorizeUserOnSessionExpiration(authenticationCookie, cookies);
    }

    @Test
    public void getCspUserIdWhenMoreThanOneUserIdExists() throws RedirectException {
        Cookie[] cookies = new Cookie [] {
                new Cookie(RestrictionAccessFilter.CSP_USER_ID, userId),
                new Cookie(RestrictionAccessFilter.CSP_USER_ID, "other")};
        assertEquals(userId, filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID));
    }

    @Test()
    public void getCspUserIdWhenUserIdIsNull() throws RedirectException {
        Cookie[] cookies = new Cookie [] {new Cookie(RestrictionAccessFilter.CSP_USER_ID, null)} ;
        assertNull(filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID));
    }

    @Test(expected=RedirectException.class)
    public void getCspUserIdWhenUserIdCookieNotFound() throws RedirectException {
        Cookie[] cookies = new Cookie [] {new Cookie("someName", "someValue")} ;
        filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID);
    }

    @Test(expected=RedirectException.class)
    public void getCspUserIdWhenNoCookiesFound() throws RedirectException {
        Cookie[] cookies = new Cookie [] {};
        filter.getCookieValue(cookies, RestrictionAccessFilter.CSP_USER_ID);
    }

    @Test(expected=RedirectException.class)
    public void getCspUserIdWhenCookiesNull() throws RedirectException {
        filter.getCookieValue(null, RestrictionAccessFilter.CSP_USER_ID);
    }


}
