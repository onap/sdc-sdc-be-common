/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.security.logging.elements;

import org.junit.Before;
import org.junit.Test;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.onap.sdc.security.logging.api.ILogConfiguration;
import org.onap.sdc.security.logging.enums.Severity;
import org.slf4j.MDC;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LogFieldsMdcHandlerTest {
    LogFieldsMdcHandler instanceMdcWrapper;
    LogFieldsMdcHandler spy;

    @Before
    public void setUp() throws Exception {
        LogFieldsMdcHandler.hostAddress="TestHost";
        LogFieldsMdcHandler.fqdn="Test";
        instanceMdcWrapper = LogFieldsMdcHandler.getInstance();
        spy = spy(instanceMdcWrapper);
    }

    @Test
    public void getInstance() {
        assertNotNull(LogFieldsMdcHandler.getInstance());
    }

    @Test
    public void startAuditTimer() {
        spy.startAuditTimer();
        verify(spy, times(1)).startAuditTimer();
        assertNotNull(MDC.get(ONAPLogConstants.MDCs.ENTRY_TIMESTAMP));
    }

    @Test
    public void startMetricTimer() {
        spy.startMetricTimer();
        verify(spy, times(1)).startMetricTimer();
        assertNotNull(MDC.get(ONAPLogConstants.MDCs.INVOKE_TIMESTAMP));
    }

    @Test
    public void stopAuditTimer() {
        spy.stopAuditTimer();
        verify(spy, times(1)).stopAuditTimer();
        assertNotNull(MDC.get(ILogConfiguration.MDC_END_TIMESTAMP));
        assertNotNull(MDC.get(ILogConfiguration.MDC_ELAPSED_TIME));
    }

    @Test
    public void stopMetricTimer() {
        spy.stopMetricTimer();
        verify(spy, times(1)).stopMetricTimer();
        assertNotNull(MDC.get(ILogConfiguration.MDC_END_TIMESTAMP));
        assertNotNull(MDC.get(ILogConfiguration.MDC_ELAPSED_TIME));
    }

    @Test
    public void setClassName() {
        spy.setClassName("TestClassName");
        verify(spy, times(1)).setClassName(anyString());
        assertEquals("TestClassName", MDC.get(ILogConfiguration.MDC_CLASS_NAME));
    }

    @Test
    public void setServerFQDN() {
        spy.setServerFQDN("TestFQDN");
        verify(spy, times(1)).setServerFQDN(anyString());
        assertEquals("TestFQDN", MDC.get(ONAPLogConstants.MDCs.SERVER_FQDN));
    }

    @Test
    public void testServerIPAddress() {
        spy.setServerIPAddress("172.0.0.0");
        verify(spy, times(1)).setServerIPAddress(anyString());
        assertEquals("172.0.0.0", MDC.get(ILogConfiguration.MDC_SERVER_IP_ADDRESS));
        assertEquals(spy.getServerIpAddress(), MDC.get(ILogConfiguration.MDC_SERVER_IP_ADDRESS));
        verify(spy, times(1)).getServerIpAddress();
    }

    @Test
    public void testServerFQDNInternally() {
        spy.setServerFQDNInternally();
        verify(spy, times(1)).setServerFQDNInternally();
        assertNotNull(MDC.get(ONAPLogConstants.MDCs.SERVER_FQDN));
        assertEquals(spy.getFqdn(), MDC.get(ONAPLogConstants.MDCs.SERVER_FQDN));
        verify(spy, times(1)).getFqdn();

    }

    @Test
    public void setServerIPAddressInternally() {

        spy.setServerIPAddressInternally();
        verify(spy, times(1)).setServerIPAddressInternally();
        assertNotNull(MDC.get(ILogConfiguration.MDC_SERVER_IP_ADDRESS));
    }

    @Test
    public void setInstanceUUID() {
        spy.setInstanceUUID("Test UUID");
        verify(spy, times(1)).setInstanceUUID(anyString());
        assertEquals("Test UUID", MDC.get(ONAPLogConstants.MDCs.INSTANCE_UUID));
    }

    @Test
    public void setProcessKey() {
        spy.setProcessKey("Test String");
        verify(spy, times(1)).setProcessKey(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_PROCESS_KEY));
    }

    @Test
    public void setAlertSeverity() {
        spy.setAlertSeverity(Severity.CRITICAL);
        verify(spy, times(1)).setAlertSeverity(any(Severity.class));
        assertEquals(String.valueOf(Severity.CRITICAL.getSeverityType()),
                MDC.get(ONAPLogConstants.MDCs.RESPONSE_SEVERITY));
    }

    @Test
    public void setOptCustomField1() {
        spy.setOptCustomField1("Test String");
        verify(spy, times(1)).setOptCustomField1(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_OPT_FIELD1));
    }

    @Test
    public void setOptCustomField2() {
        spy.setOptCustomField2("Test String");
        verify(spy, times(1)).setOptCustomField2(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_OPT_FIELD2));
    }

    @Test
    public void setOptCustomField3() {
        spy.setOptCustomField3("Test String");
        verify(spy, times(1)).setOptCustomField3(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_OPT_FIELD3));
    }

    @Test
    public void setOptCustomField4() {
        spy.setOptCustomField4("Test String");
        verify(spy, times(1)).setOptCustomField4(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_OPT_FIELD4));
    }

    @Test
    public void testKeyRequestId() {
        spy.setKeyRequestId("Test String");
        verify(spy, times(1)).setKeyRequestId(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));
        assertEquals(spy.getKeyRequestId(), MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));
        verify(spy, times(1)).getKeyRequestId();
    }

    @Test
    public void testKeyInvocationId() {
        spy.setKeyInvocationId("Test String");
        verify(spy, times(1)).setKeyInvocationId(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.INVOCATION_ID));
        assertNull(spy.getKeyInvocationId());
        verify(spy, times(1)).getKeyInvocationId();
    }

    @Test
    public void testRemoteHost() {
        spy.setRemoteHost("Test String");
        verify(spy, times(1)).setRemoteHost(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_REMOTE_HOST));
        assertEquals(spy.getRemoteHost(), MDC.get(ILogConfiguration.MDC_REMOTE_HOST));
        verify(spy, times(1)).getRemoteHost();
    }

    @Test
    public void testServiceName() {
        spy.setServiceName("Test String");
        verify(spy, times(1)).setServiceName(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.SERVICE_NAME));
        assertEquals("Test String", spy.getServiceName());
        verify(spy, times(1)).getServiceName();

    }

    @Test
    public void testStatusCode() {
        spy.setStatusCode("Test String");
        verify(spy, times(1)).setStatusCode(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE));
        spy.removeStatusCode();
        assertNull(MDC.get(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE));
        verify(spy, times(1)).removeStatusCode();
    }

    @Test
    public void testPartnerName() {
        spy.setPartnerName("Test String");
        verify(spy, times(1)).setPartnerName(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.PARTNER_NAME));
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.PARTNER_NAME));
        verify(spy, times(1)).setPartnerName(anyString());
        spy.removePartnerName();
        assertNull(MDC.get(ONAPLogConstants.MDCs.PARTNER_NAME));
        verify(spy, times(1)).removePartnerName();
    }

    @Test
    public void testResponseCode() {
        spy.setResponseCode(42);
        verify(spy, times(1)).setResponseCode(anyInt());
        assertEquals(Integer.toString(42), MDC.get(ONAPLogConstants.MDCs.RESPONSE_CODE));
        spy.removeResponseCode();
        assertNull(MDC.get(ONAPLogConstants.MDCs.RESPONSE_CODE));
        verify(spy, times(1)).removeResponseCode();
    }

    @Test
    public void testResponseDesc() {
        spy.setResponseDesc("Test String");
        verify(spy, times(1)).setResponseDesc(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.RESPONSE_DESCRIPTION));
        spy.removeResponseDesc();
        assertNull(MDC.get(ONAPLogConstants.MDCs.RESPONSE_DESCRIPTION));
        verify(spy, times(1)).removeResponseDesc();

    }

    @Test
    public void setServiceInstanceId() {
        spy.setServiceInstanceId("Test String");
        verify(spy, times(1)).setServiceInstanceId(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SERVICE_INSTANCE_ID));
        spy.removeServiceInstanceId();
        assertNull(MDC.get(ILogConfiguration.MDC_SERVICE_INSTANCE_ID));
        verify(spy, times(1)).removeServiceInstanceId();
    }

    @Test
    public void testTargetEntity() {
        spy.setTargetEntity("Test String");
        verify(spy, times(1)).setTargetEntity(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.TARGET_ENTITY));
        assertEquals(spy.getTargetEntity(), MDC.get(ONAPLogConstants.MDCs.TARGET_ENTITY));
        verify(spy, times(1)).getTargetEntity();
        spy.removeTargetEntity();
        assertNull(MDC.get(ONAPLogConstants.MDCs.TARGET_ENTITY));
        verify(spy, times(1)).removeTargetEntity();
    }

    @Test
    public void testTargetServiceName() {
        spy.setTargetServiceName("Test String");
        verify(spy, times(1)).setTargetServiceName(anyString());
        assertEquals("Test String", MDC.get(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME));
        assertEquals(spy.getTargetServiceName(), MDC.get(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME));
        verify(spy, times(1)).getTargetServiceName();
        spy.removeTargetServiceName();
        assertNull(MDC.get(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME));
        verify(spy, times(1)).removeTargetServiceName();
    }

    @Test
    public void setTargetVirtualEntity() {
        spy.setTargetVirtualEntity("Test String");
        verify(spy, times(1)).setTargetVirtualEntity(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_TARGET_VIRTUAL_ENTITY));
        spy.removeTargetVirtualEntity();
        assertNull(MDC.get(ILogConfiguration.MDC_TARGET_VIRTUAL_ENTITY));
        verify(spy, times(1)).removeTargetVirtualEntity();
    }

    @Test
    public void testErrorCode() {
        spy.setErrorCode(42);
        verify(spy, times(1)).setErrorCode(anyInt());
        assertEquals("42", MDC.get(ILogConfiguration.MDC_ERROR_CODE));
        assertEquals("42", spy.getErrorCode());
        verify(spy, times(1)).getErrorCode();
        spy.removeErrorCode();
        assertNull(MDC.get(ILogConfiguration.MDC_ERROR_CODE));
        verify(spy, times(1)).removeErrorCode();

    }

    @Test
    public void testErrorCategory() {
        spy.setErrorCategory("Test String");
        verify(spy, times(1)).setErrorCategory(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_ERROR_CATEGORY));
        assertEquals("Test String", spy.getErrorCategory());
        verify(spy, times(1)).getErrorCategory();
        spy.removeErrorCategory();
        assertNull(MDC.get(ILogConfiguration.MDC_ERROR_CATEGORY));
        verify(spy, times(1)).removeErrorCategory();
    }

    @Test
    public void clear() {
    }

    @Test
    public void isMDCParamEmpty() {
    }

    @Test
    public void getHostAddress() {
        assertNotNull(spy.getHostAddress());
        verify(spy, times(1)).getHostAddress();
    }

    @Test
    public void testAuditMessage() {
        spy.setAuditMessage("Test String");
        verify(spy, times(1)).setAuditMessage(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_AUDIT_MESSAGE));
        assertEquals("Test String", spy.getAuditMessage());
        verify(spy, times(1)).getAuditMessage();
    }

    @Test
    public void testSupportablityStatusCode() {
        spy.setSupportablityStatusCode("Test String");
        verify(spy, times(1)).setSupportablityStatusCode(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_STATUS_CODE));
        assertEquals("Test String", spy.getSupportablityStatusCode());
        verify(spy, times(1)).getSupportablityStatusCode();
        spy.removeSupportablityStatusCode();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_STATUS_CODE));
        verify(spy, times(1)).removeSupportablityStatusCode();
    }

    @Test
    public void testSupportablityAction() {
        spy.setSupportablityAction("Test String");
        verify(spy, times(1)).setSupportablityAction(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_ACTION));
        assertEquals("Test String", spy.getSupportablityAction());
        verify(spy, times(1)).getSupportablityAction();
        spy.removeSupportablityAction();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_ACTION));
        verify(spy, times(1)).removeSupportablityAction();
    }

    @Test
    public void testSupportablityCsarUUID() {
        spy.setSupportablityCsarUUID("Test String");
        verify(spy, times(1)).setSupportablityCsarUUID(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_CSAR_UUID));
        assertEquals("Test String", spy.getSupportablityCsarUUID());
        verify(spy, times(1)).getSupportablityCsarUUID();
        spy.removeSupportablityCsarUUID();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_CSAR_UUID));
        verify(spy, times(1)).removeSupportablityCsarUUID();
    }

    @Test
    public void testSupportablityCsarVersion() {
        spy.setSupportablityCsarVersion("Test String");
        verify(spy, times(1)).setSupportablityCsarVersion(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_CSAR_VERSION));
        assertEquals("Test String", spy.getSupportablityCsarVersion());
        verify(spy, times(1)).getSupportablityCsarVersion();
        spy.removeSupportablityCsarVersion();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_CSAR_VERSION));
        verify(spy, times(1)).removeSupportablityCsarVersion();
    }

    @Test
    public void getSupportablityComponentName() {
        spy.setSupportablityCsarVersion("Test String");
        verify(spy, times(1)).setSupportablityCsarVersion(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_CSAR_VERSION));
        assertEquals("Test String", spy.getSupportablityCsarVersion());
        verify(spy, times(1)).getSupportablityCsarVersion();
        spy.removeSupportablityCsarVersion();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_CSAR_VERSION));
        verify(spy, times(1)).removeSupportablityCsarVersion();
    }

    @Test
    public void getSupportablityComponentUUID() {
        spy.setSupportablityComponentUUID("Test String");
        verify(spy, times(1)).setSupportablityComponentUUID(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_COMPONENT_UUID));
        assertEquals("Test String", spy.getSupportablityComponentUUID());
        verify(spy, times(1)).getSupportablityComponentUUID();
        spy.removeSupportablityComponentUUID();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_COMPONENT_UUID));
        verify(spy, times(1)).removeSupportablityComponentUUID();
    }

    @Test
    public void getSupportablityComponentVersion() {
        spy.setSupportablityComponentVersion("Test String");
        verify(spy, times(1)).setSupportablityComponentVersion(anyString());
        assertEquals("Test String", MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_COMPONENT_VERSION));
        assertEquals("Test String", spy.getSupportablityComponentVersion());
        verify(spy, times(1)).getSupportablityComponentVersion();
        spy.removeSupportablityComponentVersion();
        assertNull(MDC.get(ILogConfiguration.MDC_SUPPORTABLITY_COMPONENT_VERSION));
        verify(spy, times(1)).removeSupportablityComponentVersion();
    }

    @Test
    public void collectRequestInfoForErrorAndDebugLogging() {
    }

    @Test
    public void addInfoForErrorAndDebugLogging() {
    }
}