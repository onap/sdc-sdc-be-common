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
package org.onap.sdc.security.logging.elements;


import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.onap.sdc.security.logging.api.ILogConfiguration;
import org.onap.sdc.security.logging.api.ILogFieldsHandler;
import org.onap.sdc.security.logging.enums.EcompLoggerErrorCode;
import org.onap.sdc.security.logging.enums.Severity;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoggerAudit extends LoggerBase {
    private static ArrayList<String> mandatoryFields = new ArrayList<>(Arrays.asList(
            ONAPLogConstants.MDCs.ENTRY_TIMESTAMP,
            ILogConfiguration.MDC_END_TIMESTAMP,
            ONAPLogConstants.MDCs.REQUEST_ID,
            ONAPLogConstants.MDCs.SERVICE_NAME,
            ONAPLogConstants.MDCs.PARTNER_NAME,
            ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE,
            ONAPLogConstants.MDCs.RESPONSE_CODE,
            ILogConfiguration.MDC_SERVICE_INSTANCE_ID,
            ONAPLogConstants.MDCs.RESPONSE_DESCRIPTION,
            ILogConfiguration.MDC_ELAPSED_TIME,
            ILogConfiguration.MDC_SERVER_IP_ADDRESS,
            ONAPLogConstants.MDCs.SERVER_FQDN));

    private static ArrayList<String> optionalFields = new ArrayList<>(Arrays.asList(
            ONAPLogConstants.MDCs.INSTANCE_UUID,
            ONAPLogConstants.MDCs.RESPONSE_SEVERITY,
            ILogConfiguration.MDC_REMOTE_HOST,
            ILogConfiguration.MDC_CLASS_NAME,
            ILogConfiguration.MDC_PROCESS_KEY,
            ILogConfiguration.MDC_OPT_FIELD1,
            ILogConfiguration.MDC_OPT_FIELD2,
            ILogConfiguration.MDC_OPT_FIELD3,
            ILogConfiguration.MDC_OPT_FIELD4));

    LoggerAudit(ILogFieldsHandler ecompMdcWrapper, Logger logger) {
        //TODO Andrey, set default marker
        super (ecompMdcWrapper, MarkerFactory.getMarker(ONAPLogConstants.Markers.ENTRY.getName()), logger);
        //put the remote host and FQDN values from another thread if they are set
        ecompMdcWrapper.setServerIPAddressInternally();
        ecompMdcWrapper.setServerFQDNInternally();
    }

    @Override
    public LoggerAudit startTimer() {
        ecompLogFieldsHandler.startAuditTimer();
        return this;
    }

    public LoggerAudit stopTimer() {
        ecompLogFieldsHandler.stopAuditTimer();
        return this;
    }

    public LoggerAudit setInstanceUUID(String instanceUUID) {
        ecompLogFieldsHandler.setInstanceUUID(instanceUUID);
        return this;
    }

    public LoggerAudit setOptClassName(String className) {
        MDC.put("ClassName", className);
        return this;
    }

    public LoggerAudit setOptProcessKey(String processKey) {
        ecompLogFieldsHandler.setProcessKey(processKey);
        return this;
    }

    public LoggerAudit setOptAlertSeverity(Severity alertSeverity) {
        ecompLogFieldsHandler.setAlertSeverity(alertSeverity);
        return this;
    }

    // log optional parameter
    public LoggerAudit setOptCustomField1(String customField1) {
        ecompLogFieldsHandler.setOptCustomField1(customField1);
        return this;
    }

    // log optional parameter
    public LoggerAudit setOptCustomField2(String customField2) {
        ecompLogFieldsHandler.setOptCustomField2(customField2);
        return this;
    }

    // log optional parameter
    public LoggerAudit setOptCustomField3(String customField3) {
        ecompLogFieldsHandler.setOptCustomField3(customField3);
        return this;
    }

    public LoggerAudit setOptCustomField4(String customField4) {
        ecompLogFieldsHandler.setOptCustomField4(customField4);
        return this;
    }

    @Override
    public LoggerAudit setKeyRequestId(String keyRequestId) {
        return (LoggerAudit) super.setKeyRequestId(keyRequestId);
    }

    @Override
    public LoggerAudit setKeyInvocationId(String keyInvocationId) {
        ecompLogFieldsHandler.setKeyInvocationId(keyInvocationId);
        return this;
    }

    public LoggerAudit setRemoteHost(String remoteHost) {
        ecompLogFieldsHandler.setRemoteHost(remoteHost);
        return this;
    }

    public LoggerAudit setServiceName(String serviceName) {
        ecompLogFieldsHandler.setServiceName(serviceName);
        return this;
    }

    public LoggerAudit setStatusCodeByResponseCode(String responseCode) {
        String respStatus = Integer.parseInt(responseCode) / 100 == 2 ? ONAPLogConstants.ResponseStatus.COMPLETE.name() : ONAPLogConstants.ResponseStatus.ERROR.name();
        ecompLogFieldsHandler.setStatusCode(respStatus);
        return this;
    }

    public LoggerAudit setStatusCode(String statusCode) {
        ecompLogFieldsHandler.setStatusCode(statusCode);
        return this;
    }


    public LoggerAudit setPartnerName(String partnerName) {
        ecompLogFieldsHandler.setPartnerName(partnerName);
        return this;
    }

    public LoggerAudit setResponseCode(EcompLoggerErrorCode responseCode) {
        ecompLogFieldsHandler.setResponseCode(responseCode.getErrorCode());
        return this;
    }

    public LoggerAudit setResponseDesc(String responseDesc) {
        ecompLogFieldsHandler.setResponseDesc(responseDesc);
        return this;
    }

    public LoggerAudit setOptServiceInstanceId(String serviceInstanceId) {
        ecompLogFieldsHandler.setServiceInstanceId(serviceInstanceId);
        return this;
    }

    public String getAuditMessage() {
        return ecompLogFieldsHandler.getAuditMessage();
    }


    @Override
    public List<String> getMandatoryFields() {
        return Collections.unmodifiableList(mandatoryFields);
    }

    @Override
    public LoggerAudit clear() {
        super.clear();
        ecompLogFieldsHandler.setServerFQDNInternally();
        ecompLogFieldsHandler.setServerIPAddressInternally();
        return this;
    }

}
