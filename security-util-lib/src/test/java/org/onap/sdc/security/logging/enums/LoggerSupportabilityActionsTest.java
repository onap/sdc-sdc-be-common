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
package org.onap.sdc.security.logging.enums;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class LoggerSupportabilityActionsTest {

    @Test
    public void testGetName() {
        assertEquals("ARCHIVE", LoggerSupportabilityActions.ARCHIVE.getName());
        assertEquals("ASSOCIATE RI TO RI",
            LoggerSupportabilityActions.ASSOCIATE_RI_TO_RI.getName());
        assertEquals("CHANGE LIFECYCLE STATE",
            LoggerSupportabilityActions.CHANGELIFECYCLESTATE.getName());
        assertEquals("ADD ARTIFACTS", LoggerSupportabilityActions.CREATE_ARTIFACTS.getName());
        assertEquals("CREATE CAPABILITY REQUIREMENTS",
            LoggerSupportabilityActions.CREATE_CAPABILITY_REQUIREMENTS.getName());
        assertEquals("ADD GROUP POLICY", LoggerSupportabilityActions.CREATE_GROUP_POLICY.getName());
        assertEquals("ADD GROUPS", LoggerSupportabilityActions.CREATE_GROUPS.getName());


    }
}
