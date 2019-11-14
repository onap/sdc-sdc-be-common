package org.onap.sdc.security;

public interface IUsersThreadLocalHolder {

    public void setUserContext(AuthenticationCookie authenticationCookie);

}
