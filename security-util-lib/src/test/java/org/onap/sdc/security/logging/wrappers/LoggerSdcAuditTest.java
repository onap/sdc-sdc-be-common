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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.onap.sdc.security.logging.enums.LogLevel;
import org.onap.sdc.security.logging.enums.Severity;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LoggerSdcAuditTest {

    LoggerSdcAudit spy;
    private Marker marker;

    @BeforeEach
    public void setUp() {
        LoggerSdcAudit audit = new LoggerSdcAudit(this.getClass());
        spy = spy(audit);
        marker = MarkerFactory.getMarker(ONAPLogConstants.Markers.ENTRY.getName());
    }

    @AfterEach
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void startLog() throws URISyntaxException {
        URI mockRequestURI = new URI("https://sdc.portal.com/mockService/user/testUser");
        URI mockBaseURI = new URI("https://sdc.portal.com/");
        UriInfo mockInfo = mock(UriInfo.class);
        when(mockInfo.getRequestUri()).thenReturn(mockRequestURI);
        when(mockInfo.getBaseUri()).thenReturn(mockBaseURI);
        ContainerRequestContext mockContext = mock(ContainerRequestContext.class);
        when(mockContext.getUriInfo()).thenReturn(mockInfo);
        spy.startLog(mockContext);
        verify(spy, times(1)).startLog(any(ContainerRequestContext.class));
        assertEquals("true", MDC.get("auditOn"));
    }

    @Test
    public void startAuditFetchLog() {
        spy.startAuditFetchLog("mockOnapPartner", "mockService");
        verify(spy, times(1)).startAuditFetchLog(anyString(), anyString());
        assertEquals("true", MDC.get("auditOn"));
    }

    @Test
    public void isFlowBeingTakenCare() {
        assertFalse(LoggerSdcAudit.isFlowBeingTakenCare());
        MDC.put("auditOn", "true");
        assertTrue(LoggerSdcAudit.isFlowBeingTakenCare());
    }

    @Test
    public void clearMyData() {
        spy.clearMyData();
        verify(spy, times(1)).clearMyData();
    }

    @Test
    public void logExit() throws URISyntaxException {
        URI mockRequestURI = new URI("https://sdc.portal.com/mockService/user/testUser");
        URI mockBaseURI = new URI("https://sdc.portal.com/");
        UriInfo mockInfo = mock(UriInfo.class);
        when(mockInfo.getRequestUri()).thenReturn(mockRequestURI);
        when(mockInfo.getBaseUri()).thenReturn(mockBaseURI);
        ContainerRequestContext mockContext = mock(ContainerRequestContext.class);
        when(mockContext.getUriInfo()).thenReturn(mockInfo);
        Response.StatusType mockStatus = mock(Response.StatusType.class);
        when(mockStatus.getStatusCode()).thenReturn(400);
        when(mockStatus.getReasonPhrase()).thenReturn("MockReasonPhrase");
        spy.logExit("172.0.0.0", mockContext, mockStatus, LogLevel.INFO, Severity.WARNING, "MockMessage", marker);
        assertEquals("false", MDC.get("auditOn"));
        verify(spy, times(1)).logExit(anyString(), any(ContainerRequestContext.class), any(Response.StatusType.class)
            , any(LogLevel.class), any(Severity.class), anyString(), any(Marker.class));
    }

    @Test
    public void logEntry() throws URISyntaxException {
        URI mockRequestURI = new URI("https://sdc.portal.com/mockService/user/testUser");
        URI mockBaseURI = new URI("https://sdc.portal.com/");
        UriInfo mockInfo = mock(UriInfo.class);
        when(mockInfo.getRequestUri()).thenReturn(mockRequestURI);
        when(mockInfo.getBaseUri()).thenReturn(mockBaseURI);
        ContainerRequestContext mockContext = mock(ContainerRequestContext.class);
        when(mockContext.getUriInfo()).thenReturn(mockInfo);
        Response.StatusType mockStatus = mock(Response.StatusType.class);
        when(mockStatus.getStatusCode()).thenReturn(400);
        when(mockStatus.getReasonPhrase()).thenReturn("MockReasonPhrase");
        spy.logEntry("172.0.0.0", mockContext, LogLevel.INFO, Severity.WARNING, "MockMessage", marker);
        assertEquals("false", MDC.get("auditOn"));
        verify(spy, times(1)).logEntry(anyString(), any(ContainerRequestContext.class)
            , any(LogLevel.class), any(Severity.class), anyString(), any(Marker.class));
    }

    @Test
    public void testLogEntry() {
        spy.logEntry(LogLevel.INFO, Severity.WARNING, "MockMessage", marker, "RequestID");
        assertEquals("false", MDC.get("auditOn"));
        verify(spy, times(1))
            .logEntry(any(LogLevel.class), any(Severity.class), anyString(), any(Marker.class), anyString());
    }
}