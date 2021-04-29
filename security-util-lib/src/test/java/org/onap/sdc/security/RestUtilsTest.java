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

package org.onap.sdc.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.onap.sdc.security.utils.RestUtils;


public class RestUtilsTest {

    @Test
    public void addBasicAuthHeaderTest() {
        Properties headers = new Properties();
        String encryptedPassword = SecurityUtil.encrypt("password").left().value();
        RestUtils.addBasicAuthHeader(headers, "userName", encryptedPassword);
        String authHeader = headers.getProperty(HttpHeaders.AUTHORIZATION);
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic"));
    }

    @Test
    public void decryptPasswordSuccessTest() {
        String decryptedPassword = "password";
        String encryptedPassword = SecurityUtil.encrypt(decryptedPassword).left().value();
        String resultPassword = RestUtils.decryptPassword(encryptedPassword);
        assertEquals(decryptedPassword, resultPassword);
    }

    @Test
    public void decryptEmptyPasswordTest() {
        assertThrows(
            IllegalArgumentException.class,
            () -> RestUtils.decryptPassword("")
        );
    }

    @Test
    public void decryptInvalidPasswordTest() {
        assertThrows(
            IllegalArgumentException.class,
            () -> RestUtils.decryptPassword("enc:9aS3AHtN_pR8QUGu-LPzHC7L8HO43WqOFx2s6nvrYrS")
        );
    }
}
