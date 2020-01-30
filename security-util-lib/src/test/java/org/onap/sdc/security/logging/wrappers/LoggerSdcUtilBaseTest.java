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
package org.onap.sdc.security.logging.wrappers;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.sdc.security.logging.enums.EcompLoggerErrorCode;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoggerSdcUtilBaseTest {
    private LoggerSdcUtilBase spy;

    @Before
    public void setUp() {
        LoggerSdcUtilBase loggerSdcUtilBase = new LoggerSdcUtilBase();
        spy = spy(loggerSdcUtilBase);
    }

    @Test
    public void getRequestIDfromHeaders() {
        String requestID = spy.getRequestIDfromHeaders(Arrays.asList(new String[]{"TestString1", "TestString2"}));
        assertNotNull(requestID);
        assertTrue(StringUtils.isNotBlank(requestID));
        assertFalse(requestID.contains("[") || requestID.contains("]"));
        verify(spy, times(1)).getRequestIDfromHeaders(anyList());
    }

    @Test
    public void convertHttpCodeToErrorCode() {
        assertEquals( EcompLoggerErrorCode.SUCCESS ,spy.convertHttpCodeToErrorCode(398));
        assertEquals(EcompLoggerErrorCode.SCHEMA_ERROR, spy.convertHttpCodeToErrorCode(409));
        assertEquals(EcompLoggerErrorCode.UNKNOWN_ERROR, spy.convertHttpCodeToErrorCode(421));
        assertEquals(EcompLoggerErrorCode.DATA_ERROR, spy.convertHttpCodeToErrorCode(415));
        assertEquals(EcompLoggerErrorCode.PERMISSION_ERROR, spy.convertHttpCodeToErrorCode(407));
        assertEquals(EcompLoggerErrorCode.AVAILABILITY_TIMEOUTS_ERROR, spy.convertHttpCodeToErrorCode(410));
        assertEquals(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, spy.convertHttpCodeToErrorCode(500));
        assertEquals(EcompLoggerErrorCode.AVAILABILITY_TIMEOUTS_ERROR, spy.convertHttpCodeToErrorCode(404));
        verify(spy, times(8)).convertHttpCodeToErrorCode(anyInt());
    }

    @Test
    public void getPartnerName() {
        assertEquals("SampleUserID", spy.getPartnerName("", "SampleUserID", "", ""));
        assertEquals("SampleOnapPartner", spy.getPartnerName("", "", "", "SampleOnapPartner"));
        //URL tests
        assertEquals("testUser", spy.getPartnerName("", "", "sdc.test.portal/catalog/user/testUser", ""));
        assertEquals("UNKNOWN", spy.getPartnerName("", "", "sdc.test.portal/catalog/user?testUser", ""));
        //UserAgentTests
        assertEquals("SampleUserID", spy.getPartnerName("SampleUserID", "", "", ""));
        assertEquals("fireFox_FE", spy.getPartnerName("firefox", "", "", ""));
        assertEquals("explorer_FE", spy.getPartnerName("msie", "", "", ""));
        assertEquals("chrome_FE", spy.getPartnerName("chrome", "", "", ""));
        //Unknown user
        assertEquals("UNKNOWN", spy.getPartnerName("", "", "", ""));
        verify(spy, times(9)).getPartnerName(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void getUrl() throws URISyntaxException {
        URI mockURI = new URI("https://sdc.portal.com/index.htm?language=en");
        UriInfo mockInfo = mock(UriInfo.class);
        when(mockInfo.getRequestUri()).thenReturn(mockURI);
        ContainerRequestContext mockContext = mock(ContainerRequestContext.class);
        when(mockContext.getUriInfo()).thenReturn(mockInfo);

        assertEquals("https://sdc.portal.com/index.htm?language=en", spy.getUrl(mockContext));
        verify(spy, times(1)).getUrl(any(ContainerRequestContext.class));
    }

    @Test
    public void getServiceName() throws URISyntaxException {
        URI mockRequestURI = new URI("https://sdc.portal.com/mockService");
        URI mockBaseURI = new URI("https://sdc.portal.com/");
        UriInfo mockInfo = mock(UriInfo.class);
        when(mockInfo.getRequestUri()).thenReturn(mockRequestURI);
        when(mockInfo.getBaseUri()).thenReturn(mockBaseURI);
        ContainerRequestContext mockContext = mock(ContainerRequestContext.class);
        when(mockContext.getUriInfo()).thenReturn(mockInfo);

        assertEquals("/mockService", spy.getServiceName(mockContext));
        verify(spy, times(1)).getServiceName(any(ContainerRequestContext.class));
    }
}