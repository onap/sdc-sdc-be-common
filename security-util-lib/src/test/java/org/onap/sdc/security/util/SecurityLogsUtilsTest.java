/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2020 Samsung. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.sdc.security.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.onap.sdc.security.utils.SecurityLogsUtils.PORTAL_TARGET_ENTITY;

import org.junit.jupiter.api.Test;
import org.onap.sdc.security.logging.elements.ErrorLogOptionalData;
import org.onap.sdc.security.utils.SecurityLogsUtils;

public class SecurityLogsUtilsTest {

    @Test
    public void testFullOptionalData() {

        ErrorLogOptionalData errorLogOptionalData =
            SecurityLogsUtils.fullOptionalData(PORTAL_TARGET_ENTITY, "/fetchRolesFromPortal");

        assertNotNull(errorLogOptionalData);


    }
}
