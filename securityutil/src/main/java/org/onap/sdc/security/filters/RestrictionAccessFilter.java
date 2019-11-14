package org.onap.sdc.security.filters;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.onap.sdc.security.*;
import org.onap.sdc.security.logs.wrappers.*;
import org.onap.sdc.security.logs.elements.*;
import org.onap.sdc.security.logs.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.onap.sdc.security.utils.SecurityLogsUtils.PORTAL_TARGET_ENTITY;
import static org.onap.sdc.security.utils.SecurityLogsUtils.fullOptionalData;

@Component("restrictionAccessFilter")
public class RestrictionAccessFilter extends SessionValidationFilter {

    private final ISessionValidationFilterConfiguration filterConfiguration;

    private static final Logger log = Logger.getLogger(RestrictionAccessFilter.class.getName());
    private static final String LOCATION_HEADER = "RedirectLocation";

    private static final String SESSION_IS_EXPIRED_MSG = "Session is expired for user %s";
    protected static final String CSP_USER_ID = "HTTP_CSP_ID";

    private PortalClient portalClient;
    private IUsersThreadLocalHolder threadLocalUtils;

    @Autowired
    public RestrictionAccessFilter(ISessionValidationFilterConfiguration configuration,
                                   IUsersThreadLocalHolder threadLocalUtils,PortalClient portalClient) {
        this.filterConfiguration = configuration;
        this.threadLocalUtils = threadLocalUtils;
        this.portalClient = portalClient;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        super.doFilter(servletRequest, servletResponse,filterChain);
    }

    @Override
    protected Cookie addRoleToCookie(Cookie cookie) throws RedirectException {
        AuthenticationCookie authenticationCookie;
        Set<String> updatedRolesSet = new HashSet<>();
        try {
            log.debug("Adding the role to the cookie.");
            authenticationCookie = getAuthenticationCookie(cookie);
            if (!CollectionUtils.isEmpty(authenticationCookie.getRoles())) {
                log.debug("Cookie already contains a role in its set of roles authenticationCookie Roles: {}",
                        authenticationCookie.getRoles());
                threadLocalUtils.setUserContext(authenticationCookie);
                return cookie;
            }
            String fetchedRole = portalClient.fetchUserRolesFromPortal(authenticationCookie.getUserID());
            log.debug("addRoleToCookie: Finished fetching user role from Portal. Adding it to the cookie");
            updatedRolesSet.add(fetchedRole);
            authenticationCookie.setRoles(updatedRolesSet);
            String changedCookieJson = RepresentationUtils.toRepresentation(authenticationCookie);
            log.debug("addRoleToCookie: Changed cookie Json: {}", changedCookieJson);
            cookie.setValue(CipherUtil.encryptPKC(changedCookieJson, getFilterConfiguration().getSecurityKey()));
            threadLocalUtils.setUserContext(authenticationCookie);
        } catch (IOException e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(), fullOptionalData(PORTAL_TARGET_ENTITY, "/fetchRolesFromPortal"),"Exception: {}", e.getMessage());
            throw new RestrictionAccessFilterException(e);
        } catch (CipherUtilException e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(), fullOptionalData(PORTAL_TARGET_ENTITY, "/fetchRolesFromPortal"),"Exception: {}", e.getMessage());
            throw new RedirectException(e);
        }
        return cookie;
    }

    private AuthenticationCookie getAuthenticationCookie(Cookie cookie) throws CipherUtilException {
        AuthenticationCookie authenticationCookie;
        String originalCookieJson = retrieveOriginalCookieJson(cookie);
        authenticationCookie = getAuthenticationCookie(originalCookieJson);
        return authenticationCookie;
    }

    @Override
    public void authorizeUserOnSessionExpiration(AuthenticationCookie authenticationCookie, Cookie[] cookies)
            throws RedirectException {
            log.debug("Portal fetch user role is enabled");
            if (!isAuthenticatedUserSimilarToCspUser(authenticationCookie, cookies) ||
                    areUserRolesChanged(authenticationCookie)) {
                String msg = String.format(SESSION_IS_EXPIRED_MSG, authenticationCookie.getUserID());
                log.debug(msg);
                throw new RedirectException(msg);
            }
    }

    @Override
    protected void handleRedirectException(HttpServletResponse httpServletResponse) throws IOException {

        httpServletResponse.setHeader(LOCATION_HEADER, filterConfiguration.getRedirectURL());
        httpServletResponse.setStatus(403);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(RepresentationUtils.toRepresentation
                ("Your session has expired. Please close the SDC tab and re-enter the SDC application."));
    }

    private boolean areUserRolesChanged(AuthenticationCookie authenticationCookie) {
        String cookieRole = "";
        if (!CollectionUtils.isEmpty(authenticationCookie.getRoles())) {
            log.debug("Cookie contains a role in its set of roles: {}", authenticationCookie.getRoles().toString());
            cookieRole = (String) authenticationCookie.getRoles().iterator().next();
        }
        // TODO: For future reference, when multi roles exist replace to something like:
        // TODO: authenticationCookie.getRoles().stream().forEach((role) -> areRolesEqual = areRolesEqual(user.getRole(), role));
        log.debug("Fetching roles from portal for user {}", authenticationCookie.getUserID());
        String portalRole = portalClient.fetchUserRolesFromPortal(authenticationCookie.getUserID());
        log.debug("{} user role on portal is {}, in the cookie is {}", portalRole, cookieRole);
        return StringUtils.isEmpty(cookieRole) || !cookieRole.equalsIgnoreCase(portalRole);
    }

    private boolean isAuthenticatedUserSimilarToCspUser(AuthenticationCookie cookie, Cookie[] cookies) throws RedirectException {
        String cspUserId = getCookieValue(cookies, CSP_USER_ID);
        if (cspUserId != null && cspUserId.equals(cookie.getUserID())) {
            log.debug("Auth and CSP user IDs are same: {}", cookie.getUserID());
            return true;
        }
        return false;
    }

    @VisibleForTesting
    public String getCookieValue(Cookie[] cookies, String name) throws RedirectException {
        if (ArrayUtils.isNotEmpty(cookies)) {
            List<Cookie> foundCookies = Arrays.stream(cookies)
                    .filter(c -> c.getName().endsWith(name))
                    .collect(Collectors.toList());
            if (foundCookies.size() > 0) {
                log.debug("getCookieValue: Found {} cookies with name: {}", foundCookies.size(), name);
                return foundCookies.get(0).getValue();
            }
        }
        throw new RedirectException("No cookies found with name " + name);
    }

    @Override
    public ISessionValidationFilterConfiguration getFilterConfiguration() {
        return filterConfiguration;
    }

    private boolean areRolesEqual(String role, String roleFromDB) {
        return !StringUtils.isEmpty(role) && role.equalsIgnoreCase(roleFromDB);
    }

    private String retrieveOriginalCookieJson(Cookie cookie) throws CipherUtilException {
        return CipherUtil.decryptPKC(cookie.getValue(), filterConfiguration.getSecurityKey());
    }

    private AuthenticationCookie getAuthenticationCookie(String originalCookieJson) {
        return RepresentationUtils.fromRepresentation(originalCookieJson, AuthenticationCookie.class);
    }

}
