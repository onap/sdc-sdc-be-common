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
package org.onap.sdc.security.logging.api;

public interface ILogConfiguration {

  String MDC_SERVICE_INSTANCE_ID = "ServiceInstanceId";
  String MDC_SERVER_IP_ADDRESS = "ServerIPAddress";
  String MDC_REMOTE_HOST = "RemoteHost";
  String MDC_AUDIT_MESSAGE = "AuditMessage";
  String MDC_END_TIMESTAMP = "EndTimestamp";
  String MDC_ELAPSED_TIME = "ElapsedTime";
  String MDC_PROCESS_KEY = "ProcessKey";
  String MDC_TARGET_VIRTUAL_ENTITY = "TargetVirtualEntity";
  String MDC_ERROR_CATEGORY = "ErrorCategory";
  String MDC_ERROR_CODE = "ErrorCode";
  String MDC_ERROR_DESC = "ErrorDescription";
  String MDC_CLASS_NAME = "ClassName";
  String MDC_OPT_FIELD1 = "CustomField1";
  String MDC_OPT_FIELD2 = "CustomField2";
  String MDC_OPT_FIELD3 = "CustomField3";
  String MDC_OPT_FIELD4 = "CustomField4";
  String MDC_SUPPORTABLITY_ACTION = "SupportablityAction";
  String MDC_SUPPORTABLITY_CSAR_UUID="SupportablityCsarUUID";
  String MDC_SUPPORTABLITY_CSAR_VERSION="SupportablityCsarVersion";
  String MDC_SUPPORTABLITY_COMPONENT_NAME = "SupportablityComponentName";
  String MDC_SUPPORTABLITY_COMPONENT_UUID = "SupportablityComponentUUID";
  String MDC_SUPPORTABLITY_COMPONENT_VERSION="SupportablityComponentVersion";
  String MDC_SUPPORTABLITY_STATUS_CODE = "SupportablityStatus";
}