package org.onap.sdc.security.filters;

import org.onap.sdc.security.*;
import org.onap.sdc.security.logs.elements.ErrorLogOptionalData;
import org.onap.sdc.security.logs.elements.LogFieldsMdcHandler;
import org.onap.sdc.security.logs.enums.EcompLoggerErrorCode;
import org.onap.sdc.security.logs.wrappers.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class SessionValidationFilter implements Filter {
    private static final Logger log = Logger.getLogger(SessionValidationFilter.class.getName());
    private ISessionValidationFilterConfiguration filterConfiguration;
    private List<String> excludedUrls;

    public abstract ISessionValidationFilterConfiguration getFilterConfiguration();
    protected abstract Cookie addRoleToCookie(Cookie updatedCookie) throws RedirectException;
    protected abstract void authorizeUserOnSessionExpiration(AuthenticationCookie authenticationCookie, Cookie[] cookies) throws RedirectException;
    protected abstract void handleRedirectException(HttpServletResponse httpResponse) throws IOException;

    @Override
    public final void init(FilterConfig filterConfig) {
        filterConfiguration = getFilterConfiguration();
        excludedUrls = filterConfiguration.getExcludedUrls();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        long starTime = System.nanoTime();
        if (log.isDebugEnabled()) {
            log.debug("SessionValidationFilter: Validation started, received request with URL {}", httpRequest.getRequestURL());
        }

        try {
            if (isValidationRequired(servletRequest, servletResponse, filterChain, httpRequest)){
                List<Cookie> cookies = extractAuthenticationCookies(httpRequest.getCookies());
                if (log.isDebugEnabled()) {
                    log.debug("doFilter: the cookies we received from extractAuthenticationCookies, before processRequest: {}", cookies);
                }

                Cookie extractedCookie = cookies.get(0);
                processRequest(httpRequest, extractedCookie);

                // response processing
                if (log.isDebugEnabled()) {
                    log.debug("SessionValidationFilter: Cookie from request {} is valid, passing request to session extension ...", httpRequest.getRequestURL());
                }
                Cookie updatedCookie = processResponse(extractedCookie);

                cleanResponseFromLeftoverCookies(httpResponse, cookies);

                if (log.isDebugEnabled()) {
                    log.debug("SessionValidationFilter: request {} passed all validations, passing request to endpoint ...", httpRequest.getRequestURL());
                }
                httpResponse.addCookie(updatedCookie);
                filterChain.doFilter(servletRequest, httpResponse);
            }
        } catch (RedirectException e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(), new ErrorLogOptionalData(),"Exception is thrown while authenticating cookie: {}", e.getMessage());
            handleRedirectException(httpResponse);
        }
        long durationSec = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - starTime);
        long durationMil = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - starTime);
        if (log.isDebugEnabled()) {
            log.debug("SessionValidationFilter: Validation ended, running time for URL {} is: {} seconds {} milliseconds", httpRequest.getPathInfo(), durationSec, durationMil);
        }
    }


    private boolean isValidationRequired(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain, HttpServletRequest httpRequest) throws IOException, ServletException, RedirectException {
        if (isUrlFromWhiteList(httpRequest)) {
            log.debug("SessionValidationFilter: URL {} excluded from access validation , passing request to endpoint ... ", httpRequest.getRequestURL());
            filterChain.doFilter(servletRequest, servletResponse);
            return false;

        } else if (!isCookiePresent(httpRequest.getCookies())) {
            //redirect to portal app
            log.debug("SessionValidationFilter: Cookie from request {} is not valid, redirecting request to portal", httpRequest.getRequestURL());
            throw new RedirectException(String.format("Cookie from request %s is not valid", httpRequest.getRequestURL() ));
        }
        return true;
    }

    private void processRequest(HttpServletRequest httpRequest, Cookie cookie) throws RedirectException {
        AuthenticationCookie authenticationCookie;
        try {
            authenticationCookie = AuthenticationCookieUtils.getAuthenticationCookie(cookie, filterConfiguration);
        }
        catch (CipherUtilException e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(), new ErrorLogOptionalData(),"SessionValidationFilter: Cookie decryption error : {}", e.getMessage());
            throw new RedirectException("Cookie decryption error");
        }
        if (AuthenticationCookieUtils.isSessionExpired(authenticationCookie, filterConfiguration)) {
            if (log.isDebugEnabled()) {
                log.debug("SessionValidationFilter:session is expired for URL {}", httpRequest.getRequestURL());
            }
            authorizeUserOnSessionExpiration(authenticationCookie, httpRequest.getCookies());
        }
        if (log.isDebugEnabled()) {
            log.debug("SessionValidationFilter: Role is valid");
        }
    }

    private Cookie processResponse(Cookie cookie) throws IOException, RedirectException {
        Cookie updatedCookie;
        try {
            updatedCookie = AuthenticationCookieUtils.updateSessionTime(cookie, filterConfiguration);
        } catch (CipherUtilException e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(), new ErrorLogOptionalData(),"SessionValidationFilter: Cookie cipher error {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("SessionValidationFilter: Cookie cipher error : {}", e.getMessage(), e);
            }
            throw new RedirectException(e);
        }
        return addRoleToCookie(updatedCookie);
    }

    private boolean isCookiePresent(Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }
        String actualCookieName = filterConfiguration.getCookieName();
        boolean isPresent = Arrays.stream(cookies).anyMatch(c -> isCookieNameMatch(actualCookieName, c));
        if (!isPresent) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(), new ErrorLogOptionalData(),"SessionValidationFilter: Session Validation Cookie missing ");
            return false;
        }
        return true;
    }

    private List<Cookie> extractAuthenticationCookies(Cookie[] cookies) {
        String actualCookieName = filterConfiguration.getCookieName();
        if (log.isDebugEnabled()) {
            Arrays.stream(cookies).forEach(c->log.debug("SessionValidationFilter: Cookie name {}", c.getName()));
            log.debug("SessionValidationFilter: Extracting authentication cookies, {} cookies in request", cookies.length);
        }
        List<Cookie> authenticationCookies = Arrays.stream(cookies).filter(c -> isCookieNameMatch(actualCookieName, c)).collect(Collectors.toList());
        if (log.isDebugEnabled()) {
            log.debug("SessionValidationFilter: Extracted {} authentication cookies from request", authenticationCookies.size());
        }
        if( authenticationCookies.size() > 1 && log.isDebugEnabled()) {
            authenticationCookies.forEach( cookie -> log.debug("SessionValidationFilter: Multiple cookies found cookie name, {} cookie value {}", cookie.getName(), cookie.getValue()));
        }
        return authenticationCookies;
    }


    // use contains for matching due issue with ecomp portal ( change cookie name, add prefix ), temp solution
    private boolean isCookieNameMatch(String actualCookieName, Cookie c) {
        return c.getName().contains(actualCookieName);
    }

    private boolean isUrlFromWhiteList(HttpServletRequest httpRequest) {
        if (httpRequest.getPathInfo() == null){
            final String servletPath = httpRequest.getServletPath().toLowerCase();
            log.debug("SessionValidationFilter: pathInfo is null, trying to check by servlet path white list validation -> ServletPath: {} ", servletPath);
            return excludedUrls.stream().
                    anyMatch(servletPath::matches);
        }
        String pathInfo = httpRequest.getPathInfo().toLowerCase();
        log.debug("SessionValidationFilter: white list validation ->  PathInfo: {} ", pathInfo);
        return excludedUrls.stream().
                anyMatch(pathInfo::matches);
    }

    private void cleanResponseFromLeftoverCookies(HttpServletResponse httpResponse, List<Cookie> cookiesList) {
        for (Cookie cookie:cookiesList){
            Cookie cleanCookie = AuthenticationCookieUtils.createUpdatedCookie(cookie, null, filterConfiguration);
            cleanCookie.setMaxAge(0);
            if (log.isDebugEnabled()) {
                log.debug("SessionValidationFilter Cleaning Cookie cookie name: {} added to response", cleanCookie.getName());
            }
            httpResponse.addCookie(cleanCookie);
        }
    }

    @Override
    public void destroy() {

    }
}
