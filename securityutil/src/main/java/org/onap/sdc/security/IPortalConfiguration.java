package org.onap.sdc.security;

/**
 * Configuration for Cookie object , have to be same over all components of application
 */
public interface IPortalConfiguration {

    String getPortalApiPrefix();
    long getMaxIdleTime();
    String getUserAttributeName();
    boolean IsUseRestForFunctionalMenu();
    String getPortalApiImplClass();
    String getRoleAccessCentralized();
    boolean getUebListenersEnable();
    String getEcompRedirectUrl();
    String getEcompRestUrl();
    String getPortalUser();
    String getPortalPass();
    String getPortalAppName();
    String getUebAppKey();
    String getAafNamespace();

    String getAuthNamespace();
    String getCspCookieName();
    String getCspGateKeeperProdKey();
    String getExtReqConnectionTimeout();
    String getExtReqReadTimeout();
}
