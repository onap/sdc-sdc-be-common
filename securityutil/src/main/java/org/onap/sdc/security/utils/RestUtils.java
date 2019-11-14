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
package org.onap.sdc.security.utils;

import com.google.common.annotations.VisibleForTesting;
import fj.data.Either;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.onap.sdc.security.SecurityUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class RestUtils {

    public static void addBasicAuthHeader(Properties headers, String username, String password) {
        headers.setProperty(HttpHeaders.AUTHORIZATION, getAuthHeaderValue(username,password));
    }

    private static String getAuthHeaderValue(String username, String password) {
        byte[] credentials = Base64.encodeBase64((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(credentials, StandardCharsets.UTF_8);
    }

    public static void addBasicAuthHeaderWithEncryptedPassword(Map<String, String> headers, String username, String password) {
        String decryptedPassword = decryptPassword(password);
        headers.put(HttpHeaders.AUTHORIZATION, getAuthHeaderValue(username,decryptedPassword));
    }

    @VisibleForTesting
    public static String decryptPassword(String password) {
        validate(password);
        Either<String, String> passkey = SecurityUtil.INSTANCE.decrypt(password);
        if(passkey.isLeft()) {
            return passkey.left().value();
        }
        else {
            throw new IllegalArgumentException(passkey.right().value());
        }

    }

    private static void validate(String str) {
        if(StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("BasicAuthorization username and/or password cannot be empty");
        }
    }

}
