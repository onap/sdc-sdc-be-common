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
package org.onap.sdc.security.logging.wrappers;


import org.apache.commons.lang3.StringUtils;
import org.onap.sdc.security.logging.elements.LoggerFactory;
import org.onap.sdc.security.logging.elements.LoggerMetric;
import org.slf4j.MDC;

public class LoggerSdcMetrics extends LoggerSdcUtilBase {

    private static String METRICS_ON = "metricsOn";
    private String className;
    private final LoggerMetric ecompLoggerMetrics;

    public LoggerSdcMetrics(Class<?> clazz) {
        this.className = clazz.getName();
        ecompLoggerMetrics = LoggerFactory.getMdcLogger(LoggerMetric.class, org.slf4j.LoggerFactory.getLogger(clazz));
    }

    public void startMetricsFetchLog(String partnerName, String serviceName) {
        ecompLoggerMetrics.clear()
                .startTimer()
                .setPartnerName(partnerName)
                .setServiceName(serviceName)
                .setOptClassName(serviceName);
        MDC.put(METRICS_ON, "true");
    }

    public static boolean isFlowBeingTakenCare() {
        String auditOn = MDC.get(METRICS_ON);
        return !StringUtils.isEmpty(auditOn) && "true".equals(auditOn);
    }

    //this function clears the MDC data that relevant for this class
    public void clearMyData(){
        ecompLoggerMetrics.clear();
    }

}

