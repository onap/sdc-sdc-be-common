package org.onap.sdc.security.utils;

import org.onap.sdc.security.logs.elements.ErrorLogOptionalData;

public class SecurityLogsUtils {
    public static String PORTAL_TARGET_ENTITY = "PORTAL";

    public static ErrorLogOptionalData fullOptionalData(String targetEntity, String targetServiceName) {
        return ErrorLogOptionalData.newBuilder().targetEntity(targetEntity)
                .targetServiceName(targetServiceName).build();
    }
}
