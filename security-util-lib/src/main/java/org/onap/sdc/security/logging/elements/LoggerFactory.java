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

import org.slf4j.Logger;

public class LoggerFactory {

    private LoggerFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T, V> V getLogger(Class<T> type, Logger logger) {

        if (type.getClass().isAssignableFrom(LoggerAudit.class)) {
            return (V) new LoggerAudit(new LogFieldsMdcHandler(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerDebug.class)) {
            return (V) new LoggerDebug(new LogFieldsMdcHandler(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerMetric.class)) {
            return (V) new LoggerMetric(new LogFieldsMdcHandler(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerError.class)) {
            return (V) new LoggerError(new LogFieldsMdcHandler(), logger);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> V getMdcLogger(Class<T> type, Logger logger) {

        if (type.getClass().isAssignableFrom(LoggerAudit.class)) {
            return (V) new LoggerAudit(LogFieldsMdcHandler.getInstance(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerDebug.class)) {
            return (V) new LoggerDebug(LogFieldsMdcHandler.getInstance(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerMetric.class)) {
            return (V) new LoggerMetric(LogFieldsMdcHandler.getInstance(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerError.class)) {
            return (V) new LoggerError(LogFieldsMdcHandler.getInstance(), logger);
        }

        if (type.getClass().isAssignableFrom(LoggerSupportability.class)) {
            return (V) new LoggerSupportability(LogFieldsMdcHandler.getInstance(), logger);
        }

        return null;
    }
}
