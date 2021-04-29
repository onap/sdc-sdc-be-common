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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.onap.sdc.security.logging.elements.ErrorLogOptionalData;
import org.onap.sdc.security.logging.enums.EcompErrorSevirity;
import org.onap.sdc.security.logging.enums.EcompLoggerErrorCode;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LoggerTest {

    Logger log;
    Logger spy;
    Marker marker;
    List<String> testList;

    @BeforeEach
    public void setUp() {
        testList = Arrays.asList("Item1", "Item2");
        marker = MarkerFactory.getMarker(ONAPLogConstants.Markers.ENTRY.getName());
        log = Logger.getLogger(LoggerTest.class.getName());
        spy = spy(log);
    }


    @Test
    public void testGetLogger() {
        assertNotNull(Logger.getLogger(LoggerTest.class));
    }

    @Test
    public void isDebugEnabled() {
        assertTrue(log.isDebugEnabled(),"Debug level not enabled");
    }

    @Test
    public void getName() {
        assertEquals("Logger name not as expected", "org.onap.sdc.security.logging.wrappers.LoggerTest", log.getName());
    }

    @Test
    public void isTraceEnabled() {
        assertFalse(spy.isTraceEnabled(),"Trace level should be enabled");
    }

    @Test
    public void isErrorEnabled() {
        assertTrue(spy.isErrorEnabled(),"Error level is not enabled");
    }

    @Test
    public void isWarnEnabled() {
        assertTrue(spy.isWarnEnabled(),"Warn level is not enabled");
    }


    @Test
    public void isInfoEnabled() {
        assertTrue(spy.isInfoEnabled(), "Info level is not enabled");
    }

    @Test
    public void info() {
        spy.info("Test Info log");
        verify(spy, times(1)).info("Test Info log");
    }

    @Test
    public void testInfoWithObject() {
        spy.info("Test Info log - {}", testList);
        verify(spy, times(1)).info("Test Info log - {}", testList);
    }

    @Test
    public void testInfoWith2Object() {
        spy.info("Test Info log - {} - {}", testList, testList);
        verify(spy, times(1)).info("Test Info log - {} - {}", testList, testList);
    }

    @Test
    public void debug() {
        spy.debug("Test DEBUG log - {}", testList);
        verify(spy, times(1)).debug(anyString(), any(Object.class));
    }

    @Test
    public void metric() {
        spy.metric("Test metric log - {}", testList);
        verify(spy, times(1)).metric(anyString(), any(Object.class));
    }

    @Test
    public void invoke() {
        spy.invoke("Test Host", "Test Entity", "Test Service", "Test metric log", testList, testList, testList);
        verify(spy, times(1))
            .invoke(anyString(), anyString(), anyString(), anyString(), anyCollection(), anyCollection(),
                anyCollection());
    }

    @Test
    public void invokeReturn() {
        spy.invokeReturn(ONAPLogConstants.ResponseStatus.ERROR, "Test metric log", testList, testList, testList);
        verify(spy, times(1))
            .invokeReturn(any(ONAPLogConstants.ResponseStatus.class), anyString(), anyCollection(), anyCollection(),
                anyCollection());
    }

    @Test
    public void testInvokeReturn() {
        spy.invokeReturn("Test metric log", testList, testList, testList);
        verify(spy, times(1)).invokeReturn(anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void invokeSynchronous() {
        spy.invokeSynchronous("Test metric log", testList, testList, testList);
        verify(spy, times(1)).invokeSynchronous(anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testDebugThrowable() {
        spy.debug("Test DEBUG throwable", new RuntimeException("Mock Exception"));
        verify(spy, times(1)).debug(anyString(), any(Throwable.class));
    }

    @Test
    public void testIsDebugEnabledMarker() {
        assertFalse(spy.isDebugEnabled(marker));
    }

    @Test
    public void testDebugMarkerMsg() {
        spy.debug(marker, "Test marker debug log");
        verify(spy, times(1)).debug(any(Marker.class), anyString());
    }

    @Test
    public void testDebugMarkerWithParam() {
        spy.debug(marker, "Test marker debug log - {}", testList);
        verify(spy, times(1)).debug(any(Marker.class), anyString(), anyCollection());
    }

    @Test
    public void testDebugMarkerWithMultipleObjects() {
        spy.debug(marker, "Test marker debug log - {} - {} - {}", testList, testList, testList);
        verify(spy, times(1)).debug(any(Marker.class), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testDebugWithMarker2Params() {
        spy.debug(marker, "Test marker debug log - {} - {}", testList, testList);
        verify(spy, times(1)).debug(any(Marker.class), anyString(), anyCollection(), anyCollection());
    }

    @Test
    public void testDebugMarkerThrowable() {
        spy.debug(marker, "Test marker debug log", new RuntimeException("Mock Exception"));
        verify(spy, times(1)).debug(any(Marker.class), anyString(), any(Throwable.class));
    }

    @Test
    public void testDebug() {
        spy.debug("Test debug log");
        verify(spy, times(1)).debug(anyString());
    }

    @Test
    public void testDebugWithParam() {
        spy.debug("Test debug log - {}", testList);
        verify(spy, times(1)).debug(anyString(), anyCollection());
    }

    @Test
    public void testDebugWith2Params() {
        spy.debug("Test debug log - {} - {}", testList, testList);
        verify(spy, times(1)).debug(anyString(), anyCollection(), anyCollection());
    }

    @Test
    public void trace() {
        spy.trace("Test trace log - {} - {} - {}", testList, testList, testList);
        verify(spy, times(1)).trace(anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testTraceWithThrowable() {
        spy.trace("Test trace log", new RuntimeException("Mock Exception"));
        verify(spy, times(1)).trace(anyString(), any(Throwable.class));
    }

    @Test
    public void testIsTraceEnabled() {
        assertFalse(spy.isTraceEnabled(marker));
    }

    @Test
    public void testTraceWithMarker() {
        spy.trace(marker, "Test trace log");
        verify(spy, times(1)).trace(any(Marker.class), anyString());
    }

    @Test
    public void testTraceMarkerWithParam() {
        spy.trace(marker, "Test trace log - {}", testList);
        verify(spy, times(1)).trace(any(Marker.class), anyString(), anyCollection());
    }

    @Test
    public void testTraceMarkerWith2Params() {
        spy.trace(marker, "Test trace log - {} - {}", testList, testList);
        verify(spy, times(1)).trace(any(Marker.class), anyString(), anyCollection(), anyCollection());
    }

    @Test
    public void testTraceMarkerWithMultipleParams() {
        spy.trace(marker, "Test trace log - {} - {} - {}", testList, testList, testList);
        verify(spy, times(1)).trace(any(Marker.class), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testTraceMarkerWithThrowable() {
        spy.trace(marker, "Test trace log", new RuntimeException("Mock Exception"));
        verify(spy, times(1)).trace(any(Marker.class), anyString(), any(Throwable.class));
    }

    @Test
    public void testTrace() {
        spy.trace("Test trace log");
        verify(spy, times(1)).trace(anyString());
    }

    @Test
    public void testTraceWithParam() {
        spy.trace("Test trace log - {}", testList);
        verify(spy, times(1)).trace(anyString(), anyCollection());
    }

    @Test
    public void testTraceWith2Params() {
        spy.trace("Test trace log - {} - {}", testList, testList);
        verify(spy, times(1)).trace(anyString(), anyCollection(), anyCollection());
    }

    @Test
    public void testInfoWithMultipleObjects() {
        spy.info("Test Info log - {} - {} - {}", testList, testList, testList);
        verify(spy, times(1)).info(anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testInfoWithThrowable() {
        spy.info("Test Info log", new RuntimeException("Mock Exception"));
        verify(spy, times(1)).info(anyString(), any(Throwable.class));
    }

    @Test
    public void testIsInfoEnabled() {
        assertFalse(spy.isInfoEnabled(marker));
    }

    @Test
    public void testInfoWithMarker() {
        spy.info(marker, "Test info log");
        verify(spy, times(1)).info(any(Marker.class), anyString());
    }

    @Test
    public void testInfoMarkerWithParam() {
        spy.info(marker, "Test info log - {}", testList);
        verify(spy, times(1)).info(any(Marker.class), anyString(), anyCollection());
    }

    @Test
    public void testInfoMarkerWith2Params() {
        spy.info(marker, "Test info log - {} - {}", testList, testList);
        verify(spy, times(1)).info(any(Marker.class), anyString(), anyCollection(), anyCollection());
    }

    @Test
    public void testInfoMarkerWithMultipleParams() {
        spy.info(marker, "Test info log - {} - {} - {}", testList, testList, testList);
        verify(spy, times(1)).info(any(Marker.class), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testInfoMarkerWithThrowable() {
        spy.info(marker, "Test info log", new RuntimeException("Mock Exception"));
        verify(spy, times(1)).info(any(Marker.class), anyString(), any(Throwable.class));
    }

    @Test
    public void testIsWarnEnabledMarker() {
        assertFalse(spy.isWarnEnabled(marker));
    }

    @Test
    public void testIsErrorEnabledMarker() {
        assertFalse(spy.isErrorEnabled(marker));
    }

    @Test
    public void testErrorWithSeverity() {
        spy.error(EcompErrorSevirity.ERROR, EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service", "MockTargetEntity",
            "Test error description", testList, testList, testList);
        verify(spy, times(1)).error(any(EcompErrorSevirity.class), any(EcompLoggerErrorCode.class),
            anyString(), anyString(), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testError() {
        spy.error(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {}", "Test error description", testList, testList, testList);
        verify(spy, times(1)).error(any(EcompLoggerErrorCode.class),
            anyString(), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testErrorOptionalData() {
        spy.error(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {} - {}", ErrorLogOptionalData.newBuilder().build(),
            "Test error description", testList, testList, testList);
        verify(spy, times(1)).error(any(EcompLoggerErrorCode.class),
            anyString(), any(ErrorLogOptionalData.class), anyString(), anyCollection(), anyCollection(),
            anyCollection());
    }

    @Test
    public void testWarn() {
        spy.warn(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {}", "Test warn description", testList, testList, testList);
        verify(spy, times(1)).warn(any(EcompLoggerErrorCode.class),
            anyString(), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testWarnWithTargetEntity() {
        spy.warn(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {} - {}", "MockTargetEntity",
            "Test warn description", testList, testList, testList);
        verify(spy, times(1)).warn(any(EcompLoggerErrorCode.class),
            anyString(), anyString(), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testWarnOptionalData() {
        spy.warn(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {} - {}", ErrorLogOptionalData.newBuilder().build(),
            "Test warn description", testList, testList, testList);
        verify(spy, times(1)).warn(any(EcompLoggerErrorCode.class),
            anyString(), any(ErrorLogOptionalData.class), anyString(), anyCollection(), anyCollection(),
            anyCollection());
    }

    @Test
    public void testFatalWithTargetEntity() {
        spy.fatal(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {} - {}", "MockTargetEntity",
            "Test fatal description", testList, testList, testList);
        verify(spy, times(1)).fatal(any(EcompLoggerErrorCode.class),
            anyString(), anyString(), anyString(), anyCollection(), anyCollection(), anyCollection());
    }

    @Test
    public void testFatalWithOptionalData() {
        spy.fatal(EcompLoggerErrorCode.UNKNOWN_ERROR,
            "Mock Service - {} - {} - {} - {} - {}", ErrorLogOptionalData.newBuilder().build(),
            "Test fatal description", testList, testList, testList);
        verify(spy, times(1)).fatal(any(EcompLoggerErrorCode.class),
            anyString(), any(ErrorLogOptionalData.class), anyString(), anyCollection(), anyCollection(),
            anyCollection());
    }
}
